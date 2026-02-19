package com.diana.matchingengine.api.controller;

import com.diana.matchingengine.api.dto.AcceptOrderResponse;
import com.diana.matchingengine.api.dto.CreateOrderRequest;
import com.diana.matchingengine.domain.model.Order;
import com.diana.matchingengine.engine.concurrency.SymbolOrderDispatcher;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final SymbolOrderDispatcher dispatcher;

    public OrderController(SymbolOrderDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AcceptOrderResponse create(@Valid @RequestBody CreateOrderRequest req) {
        Order order = Order.createNew(req.getSide(), req.getSymbol(), req.getPrice(), req.getQuantity());
        dispatcher.submit(order);
        return new AcceptOrderResponse(order.getId());
    }
}
