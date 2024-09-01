package com.sdragon.aggregatorService.dto;

import com.sdragon.aggregatorService.domain.Ticker;
import com.sdragon.aggregatorService.domain.TradeAction;

public record StockTradeRequest(Ticker ticker,
                                Integer price,
                                Integer quantity,
                                TradeAction action) {

    public Integer totalPrice() {
        return price * quantity;
    }
}
