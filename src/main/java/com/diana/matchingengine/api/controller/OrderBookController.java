package com.diana.matchingengine.api.controller;

import com.diana.matchingengine.api.dto.OrderBookResponse;
import com.diana.matchingengine.domain.model.Order;
import com.diana.matchingengine.engine.book.OrderBook;
import com.diana.matchingengine.service.OrderBookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/orderbook")
public class OrderBookController {

    private final OrderBookService service;

    public OrderBookController(OrderBookService service) {
        this.service = service;
    }

    @GetMapping("/{symbol}")
    public OrderBookResponse get(@PathVariable String symbol) {
        OrderBook book = service.getBook(symbol);
        if (book == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No order book for symbol: " + symbol);
        }

        List<Order> bids = book.snapshotBids().stream()
                .sorted(Comparator.comparing(Order::getPrice).reversed()
                        .thenComparing(Order::getCreatedAt))
                .toList();

        List<Order> asks = book.snapshotAsks().stream()
                .sorted(Comparator.comparing(Order::getPrice)
                        .thenComparing(Order::getCreatedAt))
                .toList();

        return new OrderBookResponse(symbol, bids, asks);
    }
}
