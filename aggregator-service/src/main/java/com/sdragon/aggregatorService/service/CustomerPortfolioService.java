package com.sdragon.aggregatorService.service;

import com.sdragon.aggregatorService.client.CustomerServiceClient;
import com.sdragon.aggregatorService.client.StockServiceClient;
import com.sdragon.aggregatorService.dto.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerPortfolioService {

    private final StockServiceClient stockServiceClient;
    private final CustomerServiceClient customerServiceClient;

    public CustomerPortfolioService(StockServiceClient stockServiceClient, CustomerServiceClient customerServiceClient) {
        this.stockServiceClient = stockServiceClient;
        this.customerServiceClient = customerServiceClient;
    }

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId) {
        return customerServiceClient.getCustomerInformation(customerId);
    }

    public Mono<StockTradeResponse> trade(Integer customerId, TradeRequest request) {
        return stockServiceClient.getStockPrice(request.ticker())
                .map(StockPriceResponse::price)
                .map(price -> toStockTradeRequest(request, price))
                .flatMap(req -> customerServiceClient.trade(customerId, req));
    }

    private StockTradeRequest toStockTradeRequest(TradeRequest request, Integer price) {
        return new StockTradeRequest(request.ticker(), price, request.quantity(), request.action());
    }
}
