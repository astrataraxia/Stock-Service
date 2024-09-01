package com.sdragon.aggregatorService.dto;

import com.sdragon.aggregatorService.domain.Ticker;
import com.sdragon.aggregatorService.domain.TradeAction;

public record StockTradeResponse(Integer customerId,
                                 Ticker ticker,
                                 Integer price,
                                 Integer quantity,
                                 TradeAction action,
                                 Integer totalPrice,
                                 Integer balance) {
}
