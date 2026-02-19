package com.diana.matchingengine.infra.repository;

import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MetricsStore {

    // Start moment for rate calculations (app lifetime)
    private final long startEpochNanos = System.nanoTime();

    private final AtomicLong totalOrders = new AtomicLong(0);
    private final AtomicLong totalTrades = new AtomicLong(0);
    private final AtomicLong totalMatchNanos = new AtomicLong(0);

    public void incOrders() { totalOrders.incrementAndGet(); }
    public void addTrades(long n) { totalTrades.addAndGet(n); }
    public void addMatchNanos(long nanos) { totalMatchNanos.addAndGet(nanos); }

    public long getTotalOrders() { return totalOrders.get(); }
    public long getTotalTrades() { return totalTrades.get(); }
    public long getTotalMatchNanos() { return totalMatchNanos.get(); }

    public long getStartEpochNanos() { return startEpochNanos; }

    public long getUptimeNanos() {
        return System.nanoTime() - startEpochNanos;
    }

    public double getUptimeSeconds() {
        return getUptimeNanos() / 1_000_000_000.0;
    }
}
