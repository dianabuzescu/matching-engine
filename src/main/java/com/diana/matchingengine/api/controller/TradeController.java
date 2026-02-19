package com.diana.matchingengine.api.controller;

import com.diana.matchingengine.domain.model.Trade;
import com.diana.matchingengine.infra.repository.TradeStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TradeController {

    private final TradeStore tradeStore;

    public TradeController(TradeStore tradeStore) {
        this.tradeStore = tradeStore;
    }

    @GetMapping("/trades")
    public List<Trade> getAll() {
        return tradeStore.getAll();
    }
}
