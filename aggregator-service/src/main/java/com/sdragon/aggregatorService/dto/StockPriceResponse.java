package com.sdragon.aggregatorService.dto;

import com.sdragon.aggregatorService.domain.Ticker;

import java.time.LocalDateTime;

public record StockPriceResponse(Ticker ticker,
                                 Integer price) {

}
