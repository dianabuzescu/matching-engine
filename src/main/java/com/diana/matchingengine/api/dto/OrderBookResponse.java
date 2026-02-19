package com.diana.matchingengine.api.dto;

import com.diana.matchingengine.domain.model.Order;

import java.util.List;

public class OrderBookResponse {
    private final String symbol;
    private final List<Order> bids;
    private final List<Order> asks;

    public OrderBookResponse(String symbol, List<Order> bids, List<Order> asks) {
        this.symbol = symbol;
        this.bids = bids;
        this.asks = asks;
    }

    public String getSymbol() { return symbol; }
    public List<Order> getBids() { return bids; }
    public List<Order> getAsks() { return asks; }
}
