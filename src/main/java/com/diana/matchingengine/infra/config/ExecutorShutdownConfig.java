package com.diana.matchingengine.infra.config;

import com.diana.matchingengine.engine.concurrency.SymbolOrderDispatcher;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ExecutorShutdownConfig {

    private final SymbolOrderDispatcher dispatcher;

    public ExecutorShutdownConfig(SymbolOrderDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @PreDestroy
    public void onShutdown() {
        dispatcher.shutdownAll();
    }
}
