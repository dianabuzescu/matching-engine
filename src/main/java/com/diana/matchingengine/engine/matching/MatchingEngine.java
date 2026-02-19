package com.diana.matchingengine.engine.matching;

import com.diana.matchingengine.domain.enums.OrderSide;
import com.diana.matchingengine.domain.model.Order;
import com.diana.matchingengine.domain.model.Trade;
import com.diana.matchingengine.engine.book.OrderBook;

import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {

    /**
     * Runs matching for an OrderBook.
     * Returns the list of generated trades.
     */
    public List<Trade> match(OrderBook book, String symbol) {
        List<Trade> trades = new ArrayList<>();

        while (book.bestBid() != null && book.bestAsk() != null) {
            Order bid = book.bestBid(); // BUY
            Order ask = book.bestAsk(); // SELL

            // No more matching if best BUY < best SELL
            if (bid.getPrice().compareTo(ask.getPrice()) < 0) {
                break;
            }

            long qty = Math.min(bid.getQuantity(), ask.getQuantity());

            Trade trade = Trade.create(symbol, ask.getPrice(), qty, bid.getId(), ask.getId());
            trades.add(trade);

            book.popBestBid();
            book.popBestAsk();

            long bidRemaining = bid.getQuantity() - qty;
            long askRemaining = ask.getQuantity() - qty;

            if (bidRemaining > 0) {
                book.add(bid.withQuantity(bidRemaining));
            }
            if (askRemaining > 0) {
                book.add(ask.withQuantity(askRemaining));
            }
        }

        return trades;
    }
}
