package com.diana.matchingengine.api.dto;

import com.diana.matchingengine.domain.enums.OrderSide;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateOrderRequest {

    @NotNull
    private OrderSide side;

    @NotBlank
    private String symbol;

    @NotNull
    @Min(1)
    private BigDecimal price;

    @Min(1)
    private long quantity;

    public OrderSide getSide() { return side; }
    public void setSide(OrderSide side) { this.side = side; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }
}
