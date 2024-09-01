package com.sdragon.aggregatorService.dto;

import com.sdragon.aggregatorService.domain.Ticker;

public record Holding(Ticker ticker,
                      Integer quantity ) {
}
