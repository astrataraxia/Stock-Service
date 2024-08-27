package com.sydragon.customerportfolio.dto;

import com.sydragon.customerportfolio.domain.Ticker;
import com.sydragon.customerportfolio.domain.TradeAction;

public record StockTradeRequest(Ticker ticker,
                                Integer price,
                                Integer quantity,
                                TradeAction action) {
}
