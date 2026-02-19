package com.diana.matchingengine.api.controller;

import com.diana.matchingengine.infra.repository.MetricsStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MetricsController {

    private final MetricsStore metrics;

    public MetricsController(MetricsStore metrics) {
        this.metrics = metrics;
    }

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        long orders = metrics.getTotalOrders();
        long trades = metrics.getTotalTrades();
        long matchNanos = metrics.getTotalMatchNanos();

        double uptimeSeconds = metrics.getUptimeSeconds();
        double ordersPerSec = uptimeSeconds <= 0.0 ? 0.0 : orders / uptimeSeconds;
        double tradesPerSec = uptimeSeconds <= 0.0 ? 0.0 : trades / uptimeSeconds;

        double avgMatchMicrosPerOrder = orders == 0 ? 0.0 : (matchNanos / 1000.0) / orders;

        return Map.of(
                "uptimeSeconds", uptimeSeconds,
                "totalOrders", orders,
                "totalTrades", trades,
                "ordersPerSecond", ordersPerSec,
                "tradesPerSecond", tradesPerSec,
                "totalMatchNanos", matchNanos,
                "avgMatchMicrosPerOrder", avgMatchMicrosPerOrder
        );
    }
}
