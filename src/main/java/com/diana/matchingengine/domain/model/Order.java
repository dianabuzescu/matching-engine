package com.diana.matchingengine.domain.model;

import com.diana.matchingengine.domain.enums.OrderSide;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Order {
    private final String id;
    private final OrderSide side;
    private final String symbol;
    private final BigDecimal price;
    private final long quantity;          // remaining quantity
    private final Instant createdAt;

    public Order(String id, OrderSide side, String symbol, BigDecimal price, long quantity, Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.side = Objects.requireNonNull(side);
        this.symbol = Objects.requireNonNull(symbol);
        this.price = Objects.requireNonNull(price);
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.quantity = quantity;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public static Order createNew(OrderSide side, String symbol, BigDecimal price, long quantity) {
        return new Order(UUID.randomUUID().toString(), side, symbol, price, quantity, Instant.now());
    }

    public Order withQuantity(long newQuantity) {
        return new Order(this.id, this.side, this.symbol, this.price, newQuantity, this.createdAt);
    }

    public String getId() { return id; }
    public OrderSide getSide() { return side; }
    public String getSymbol() { return symbol; }
    public BigDecimal getPrice() { return price; }
    public long getQuantity() { return quantity; }
    public Instant getCreatedAt() { return createdAt; }
}
