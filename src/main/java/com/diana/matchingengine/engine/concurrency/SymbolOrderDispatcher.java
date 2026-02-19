package com.diana.matchingengine.engine.concurrency;

import com.diana.matchingengine.domain.model.Order;
import com.diana.matchingengine.domain.model.Trade;
import com.diana.matchingengine.engine.book.OrderBook;
import com.diana.matchingengine.engine.matching.MatchingEngine;
import com.diana.matchingengine.infra.repository.MetricsStore;
import com.diana.matchingengine.infra.repository.TradeStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SymbolOrderDispatcher {

    private final Map<String, BlockingQueue<Order>> queues = new ConcurrentHashMap<>();
    private final Map<String, OrderBook> books = new ConcurrentHashMap<>();
    private final Map<String, ExecutorService> workers = new ConcurrentHashMap<>();

    // per-symbol in-flight counter
    private final Map<String, AtomicInteger> inFlight = new ConcurrentHashMap<>();

    private final MatchingEngine engine = new MatchingEngine();
    private final TradeStore tradeStore;
    private final MetricsStore metricsStore;

    public SymbolOrderDispatcher(TradeStore tradeStore, MetricsStore metricsStore) {
        this.tradeStore = tradeStore;
        this.metricsStore = metricsStore;
    }

    public void submit(Order order) {
        String symbol = order.getSymbol();

        queues.computeIfAbsent(symbol, s -> new LinkedBlockingQueue<>());
        books.computeIfAbsent(symbol, s -> new OrderBook());
        inFlight.computeIfAbsent(symbol, s -> new AtomicInteger(0));

        workers.computeIfAbsent(symbol, s -> {
            ExecutorService single = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r);
                t.setName("worker-" + s);
                t.setDaemon(true);
                return t;
            });
            single.submit(() -> runLoop(s));
            return single;
        });

        queues.get(symbol).offer(order);
    }

    private void runLoop(String symbol) {
        BlockingQueue<Order> q = queues.get(symbol);
        OrderBook book = books.get(symbol);
        AtomicInteger inflightCounter = inFlight.get(symbol);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Order order = q.take();

                // mark in-flight
                inflightCounter.incrementAndGet();

                metricsStore.incOrders();

                book.add(order);

                long start = System.nanoTime();
                List<Trade> trades = engine.match(book, symbol);
                long elapsed = System.nanoTime() - start;

                metricsStore.addMatchNanos(elapsed);

                if (!trades.isEmpty()) {
                    metricsStore.addTrades(trades.size());
                    tradeStore.addAll(trades);
                }

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
            } finally {
                AtomicInteger c = inFlight.get(symbol);
                if (c != null && c.get() > 0) {
                    c.decrementAndGet();
                }
            }
        }
    }

    public OrderBook getBook(String symbol) {
        return books.get(symbol);
    }

    public int queueSize(String symbol) {
        BlockingQueue<Order> q = queues.get(symbol);
        return (q == null) ? 0 : q.size();
    }

    public int inFlightCount(String symbol) {
        AtomicInteger c = inFlight.get(symbol);
        return (c == null) ? 0 : c.get();
    }

    public boolean isIdle(String symbol) {
        return queueSize(symbol) == 0 && inFlightCount(symbol) == 0;
    }

    public void shutdownAll() {
        for (ExecutorService ex : workers.values()) {
            ex.shutdownNow();
        }
    }
}
