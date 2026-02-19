package com.diana.matchingengine.engine.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SymbolLockRegistry {

    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public ReentrantLock getLock(String symbol) {
        return locks.computeIfAbsent(symbol, s -> new ReentrantLock());
    }
}
