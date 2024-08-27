package com.sydragon.customerportfolio.entity;

import com.sydragon.customerportfolio.domain.Ticker;
import org.springframework.data.annotation.Id;

public class PortfolioItem {

    @Id
    private Integer id;
    private Integer customerId;
    private Ticker ticker;
    private Integer quantity;

    public Integer id() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer customerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Ticker ticker() {
        return ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }

    public Integer quantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
