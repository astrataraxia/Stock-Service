package com.sydragon.customerportfolio.dto;

import com.sydragon.customerportfolio.domain.Ticker;
import com.sydragon.customerportfolio.domain.TradeAction;

public record StockTradeResponse(Integer customerId,
                                 Ticker ticker,
                                 Integer price,
                                 Integer quantity,
                                 TradeAction action,
                                 Integer totalPrice,
                                 Integer balance) {
}
