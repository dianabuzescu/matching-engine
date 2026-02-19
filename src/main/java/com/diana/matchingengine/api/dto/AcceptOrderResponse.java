package com.diana.matchingengine.api.dto;

public class AcceptOrderResponse {
    private final String orderId;

    public AcceptOrderResponse(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
