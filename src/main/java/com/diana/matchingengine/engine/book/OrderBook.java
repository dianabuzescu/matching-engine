package com.diana.matchingengine.engine.book;

import com.diana.matchingengine.domain.enums.OrderSide;
import com.diana.matchingengine.domain.model.Order;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;

public class OrderBook {

    // BUY: best price first (higher price), then earlier time
    private final PriorityQueue<Order> bids = new PriorityQueue<>(
            Comparator
                    .comparing(Order::getPrice, Comparator.reverseOrder())
                    .thenComparing(Order::getCreatedAt)
    );


    // SELL: best price first (lower price), then earlier time
    private final PriorityQueue<Order> asks = new PriorityQueue<>(
            Comparator
                    .comparing(Order::getPrice)
                    .thenComparing(Order::getCreatedAt)
    );


    public void add(Order order) {
        if (order.getSide() == OrderSide.BUY) {
            bids.add(order);
        } else {
            asks.add(order);
        }
    }

    public PriorityQueue<Order> getBids() { return bids; }
    public PriorityQueue<Order> getAsks() { return asks; }

    public Order bestBid() { return bids.peek(); }
    public Order bestAsk() { return asks.peek(); }

    public Order popBestBid() { return bids.poll(); }
    public Order popBestAsk() { return asks.poll(); }

    public List<Order> snapshotBids() {
        return new ArrayList<>(bids);
    }

    public List<Order> snapshotAsks() {
        return new ArrayList<>(asks);
    }

}
