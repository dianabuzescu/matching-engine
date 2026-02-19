package com.diana.matchingengine.api.controller;

import com.diana.matchingengine.domain.enums.OrderSide;
import com.diana.matchingengine.domain.model.Order;
import com.diana.matchingengine.engine.concurrency.SymbolOrderDispatcher;
import com.diana.matchingengine.infra.repository.TradeStore;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/load")
public class LoadTestController {

    private final SymbolOrderDispatcher dispatcher;
    private final TradeStore tradeStore;

    public LoadTestController(SymbolOrderDispatcher dispatcher, TradeStore tradeStore) {
        this.dispatcher = dispatcher;
        this.tradeStore = tradeStore;
    }

    @PostMapping
    public Map<String, Object> load(@RequestParam(defaultValue = "AAPL") String symbol,
                                    @RequestParam(defaultValue = "1000") int n,
                                    @RequestParam(defaultValue = "false") boolean wait,
                                    @RequestParam(defaultValue = "10000") long timeoutMs) {

        // trades before
        int tradesBefore = tradeStore.getAll().size();

        long submitStartNanos = System.nanoTime();

        // SUBMIT
        for (int i = 0; i < n; i++) {
            OrderSide side = (i % 2 == 0) ? OrderSide.BUY : OrderSide.SELL;

            BigDecimal price = (side == OrderSide.BUY)
                    ? new BigDecimal("101.00")
                    : new BigDecimal("100.00");

            long qty = ThreadLocalRandom.current().nextInt(1, 4);

            dispatcher.submit(Order.createNew(side, symbol, price, qty));
        }

        double submitMillis = (System.nanoTime() - submitStartNanos) / 1_000_000.0;

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("symbol", symbol);
        resp.put("submitted", n);
        resp.put("submitMillis", submitMillis);

        if (!wait) {
            return resp;
        }

        // WAIT UNTIL IDLE
        long waitStartNanos = System.nanoTime();
        long timeoutNanos = timeoutMs * 1_000_000L;

        while (!dispatcher.isIdle(symbol)) {
            if ((System.nanoTime() - waitStartNanos) > timeoutNanos) {
                resp.put("waitTimedOut", true);
                resp.put("queueSizeAtTimeout", dispatcher.queueSize(symbol));
                resp.put("inFlightAtTimeout", dispatcher.inFlightCount(symbol));
                break;
            }
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                resp.put("interrupted", true);
                break;
            }
        }

        // post-submit drain time
        double drainMillis = (System.nanoTime() - waitStartNanos) / 1_000_000.0;
        resp.put("drainMillis", drainMillis);

        // TRUE end-to-end time (submit start -> idle)
        double endToEndMillis = (System.nanoTime() - submitStartNanos) / 1_000_000.0;
        resp.put("endToEndMillis", endToEndMillis);

        // trades delta
        int tradesAfter = tradeStore.getAll().size();
        int tradesDelta = Math.max(0, tradesAfter - tradesBefore);
        resp.put("tradesGeneratedDelta", tradesDelta);

        // throughput based on TRUE end-to-end
        double endToEndSeconds = endToEndMillis / 1000.0;
        double endToEndOrdersPerSecond = endToEndSeconds <= 0.0 ? 0.0 : (n / endToEndSeconds);
        resp.put("endToEndOrdersPerSecond", endToEndOrdersPerSecond);

        double endToEndTradesPerSecond = endToEndSeconds <= 0.0 ? 0.0 : (tradesDelta / endToEndSeconds);
        resp.put("endToEndTradesPerSecond", endToEndTradesPerSecond);


        return resp;

    }
}
