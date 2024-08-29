package com.sydragon.customerportfolio.mapper;

import com.sydragon.customerportfolio.domain.Ticker;
import com.sydragon.customerportfolio.dto.CustomerInformation;
import com.sydragon.customerportfolio.dto.Holding;
import com.sydragon.customerportfolio.dto.StockTradeRequest;
import com.sydragon.customerportfolio.dto.StockTradeResponse;
import com.sydragon.customerportfolio.entity.Customer;
import com.sydragon.customerportfolio.entity.PortfolioItem;

import java.util.List;

public class EntityDtoMapper {

    public static CustomerInformation toCustomerInformation(Customer customer, List<PortfolioItem> items) {
        List<Holding> holdings = items.stream()
                .map(i -> new Holding(i.ticker(), i.quantity()))
                .toList();
        return new CustomerInformation(
                customer.id(),
                customer.name(),
                customer.balance(),
                holdings
        );
    }

    public static PortfolioItem toPortfolioItem(Integer customerId, Ticker ticker) {
        var portfolioItem = new PortfolioItem();
        portfolioItem.setCustomerId(customerId);
        portfolioItem.setTicker(ticker);
        portfolioItem.setQuantity(0);
        return portfolioItem;
    }

    public static StockTradeResponse toStockTradeResponse(StockTradeRequest request,
                                                          Integer customerId, Integer balance) {
        return new StockTradeResponse(
                customerId,
                request.ticker(),
                request.price(),
                request.quantity(),
                request.action(),
                request.totalPrice(),
                balance
        );
    }

}
