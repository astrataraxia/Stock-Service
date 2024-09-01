package com.sdragon.aggregatorService.dto;

import com.sdragon.aggregatorService.domain.Ticker;
import com.sdragon.aggregatorService.domain.TradeAction;

public record TradeRequest(Ticker ticker,
                           TradeAction action,
                           Integer quantity) {
}
