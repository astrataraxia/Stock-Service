package com.sdragon.aggregatorService.dto;

import com.sdragon.aggregatorService.domain.Ticker;

import java.time.LocalDateTime;

public record PriceUpdate(Ticker ticker,
                          Integer price,
                          LocalDateTime time) {
}
