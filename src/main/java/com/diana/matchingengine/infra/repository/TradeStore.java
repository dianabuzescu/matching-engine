package com.diana.matchingengine.infra.repository;

import com.diana.matchingengine.domain.model.Trade;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
public class TradeStore {

    private final Queue<Trade> trades = new ConcurrentLinkedQueue<>();

    public void addAll(List<Trade> newTrades) {
        trades.addAll(newTrades);
    }

    public List<Trade> getAll() {
        return new ArrayList<>(trades);
    }
}
