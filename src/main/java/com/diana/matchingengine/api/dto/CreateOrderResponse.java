package com.diana.matchingengine.api.dto;

import com.diana.matchingengine.domain.model.Order;
import com.diana.matchingengine.domain.model.Trade;

import java.util.List;

public class CreateOrderResponse {
    private final Order order;
    private final List<Trade> trades;

    public CreateOrderResponse(Order order, List<Trade> trades) {
        this.order = order;
        this.trades = trades;
    }

    public Order getOrder() { return order; }
    public List<Trade> getTrades() { return trades; }
}
