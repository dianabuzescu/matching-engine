package com.diana.matchingengine.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Trade {
    private final String id;
    private final String symbol;
    private final BigDecimal price;
    private final long quantity;
    private final String buyOrderId;
    private final String sellOrderId;
    private final Instant executedAt;

    public Trade(String id, String symbol, BigDecimal price, long quantity,
                 String buyOrderId, String sellOrderId, Instant executedAt) {
        this.id = Objects.requireNonNull(id);
        this.symbol = Objects.requireNonNull(symbol);
        this.price = Objects.requireNonNull(price);
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.quantity = quantity;
        this.buyOrderId = Objects.requireNonNull(buyOrderId);
        this.sellOrderId = Objects.requireNonNull(sellOrderId);
        this.executedAt = Objects.requireNonNull(executedAt);
    }

    public static Trade create(String symbol, BigDecimal price, long quantity, String buyOrderId, String sellOrderId) {
        return new Trade(UUID.randomUUID().toString(), symbol, price, quantity, buyOrderId, sellOrderId, Instant.now());
    }

    public String getId() { return id; }
    public String getSymbol() { return symbol; }
    public BigDecimal getPrice() { return price; }
    public long getQuantity() { return quantity; }
    public String getBuyOrderId() { return buyOrderId; }
    public String getSellOrderId() { return sellOrderId; }
    public Instant getExecutedAt() { return executedAt; }
}
