package com.diana.matchingengine;

import com.diana.matchingengine.domain.enums.OrderSide;
import com.diana.matchingengine.domain.model.Order;
import com.diana.matchingengine.engine.book.OrderBook;
import com.diana.matchingengine.infra.repository.TradeStore;
import com.diana.matchingengine.service.OrderBookService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrencyMatchingTest {

    @Test
    void concurrentOrders_sameSymbol_shouldNotCorruptBook() throws Exception {
        TradeStore tradeStore = new TradeStore();
        OrderBookService service = new OrderBookService(tradeStore);

        String symbol = "AAPL";
        int pairs = 200; // 200 buy + 200 sell
        ExecutorService pool = Executors.newFixedThreadPool(8);

        CountDownLatch ready = new CountDownLatch(pairs * 2);
        CountDownLatch start = new CountDownLatch(1);

        List<Callable<Void>> tasks = new ArrayList<>();

        // 200 BUY orders @ 101 qty 1
        for (int i = 0; i < pairs; i++) {
            tasks.add(() -> {
                ready.countDown();
                start.await();
                service.addOrderAndMatch(Order.createNew(OrderSide.BUY, symbol, new BigDecimal("101.00"), 1));
                return null;
            });
        }

        // 200 SELL orders @ 100 qty 1  -> should match all buys
        for (int i = 0; i < pairs; i++) {
            tasks.add(() -> {
                ready.countDown();
                start.await();
                service.addOrderAndMatch(Order.createNew(OrderSide.SELL, symbol, new BigDecimal("100.00"), 1));
                return null;
            });
        }

        // submit
        List<Future<Void>> futures = new ArrayList<>();
        for (Callable<Void> t : tasks) {
            futures.add(pool.submit(t));
        }

        // wait until all threads are ready, then start together
        ready.await(5, TimeUnit.SECONDS);
        start.countDown();

        // ensure no exceptions
        for (Future<Void> f : futures) {
            f.get(10, TimeUnit.SECONDS);
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        // Verify: should have exactly 200 trades
        assertEquals(pairs, tradeStore.getAll().size(), "All orders should match into trades");

        // Verify: orderbook empty (no remaining bids/asks)
        OrderBook book = service.getBook(symbol);
        assertNotNull(book);

        assertTrue(book.getBids().isEmpty(), "No remaining bids expected");
        assertTrue(book.getAsks().isEmpty(), "No remaining asks expected");
    }
}
