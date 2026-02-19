package com.diana.matchingengine.service;

import com.diana.matchingengine.engine.book.OrderBook;
import com.diana.matchingengine.engine.concurrency.SymbolOrderDispatcher;
import org.springframework.stereotype.Service;

@Service
public class OrderBookService {

    private final SymbolOrderDispatcher dispatcher;

    public OrderBookService(SymbolOrderDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public OrderBook getBook(String symbol) {
        return dispatcher.getBook(symbol);
    }
}
