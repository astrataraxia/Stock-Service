package com.sydragon.customerportfolio.dto;

import com.sydragon.customerportfolio.domain.Ticker;

public record Holding(Ticker ticker,
                      Integer quantity ) {
}
