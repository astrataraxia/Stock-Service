package com.sydragon.customerportfolio.service;

import com.sydragon.customerportfolio.dto.StockTradeRequest;
import com.sydragon.customerportfolio.dto.StockTradeResponse;
import com.sydragon.customerportfolio.entity.Customer;
import com.sydragon.customerportfolio.entity.PortfolioItem;
import com.sydragon.customerportfolio.exceptions.ApplicationException;
import com.sydragon.customerportfolio.mapper.EntityDtoMapper;
import com.sydragon.customerportfolio.repository.CustomerRepository;
import com.sydragon.customerportfolio.repository.PortfolioItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class TradeService {

    private final CustomerRepository customerRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public TradeService(CustomerRepository customerRepository, PortfolioItemRepository portfolioItemRepository) {
        this.customerRepository = customerRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    @Transactional
    public Mono<StockTradeResponse> trade(Integer customerId, StockTradeRequest request) {
        return switch (request.action()) {
            case BUY -> buyStock(customerId, request);
            case SELL -> sellStock(customerId, request);
        };
    }

    private Mono<StockTradeResponse> buyStock(Integer customerId, StockTradeRequest request) {
        var customerMono = customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationException.customerNotFound(customerId))
                .filter(c -> c.balance() >= request.totalPrice())
                .switchIfEmpty(ApplicationException.insufficientBalance(customerId));

        var portfolioItemMono = portfolioItemRepository
                .findByCustomerIdAndTicker(customerId, request.ticker())
                .defaultIfEmpty(EntityDtoMapper.toPortfolioItem(customerId, request.ticker()));

        return customerMono.zipWhen(customer -> portfolioItemMono)
                .flatMap(t -> executeBuy(t.getT1(), t.getT2(), request));
    }

    private Mono<StockTradeResponse> sellStock(Integer customerId, StockTradeRequest request) {
        var customerMono = customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationException.customerNotFound(customerId));

        var portfolioItemMono = portfolioItemRepository
                .findByCustomerIdAndTicker(customerId, request.ticker())
                .filter(item -> item.quantity() >= request.quantity())
                .switchIfEmpty(ApplicationException.insufficientShares(customerId));

        return customerMono.zipWhen(customer -> portfolioItemMono)
                .flatMap(t -> executeSell(t.getT1(), t.getT2(), request));
    }

    private Mono<StockTradeResponse> executeBuy(Customer customer, PortfolioItem portfolioItem,
                                                StockTradeRequest request) {
        customer.setBalance(customer.balance() - request.totalPrice());
        portfolioItem.setQuantity(portfolioItem.quantity() + request.quantity());
        return saveAndBuildResponse(customer, portfolioItem, request);
    }

    private Mono<StockTradeResponse> executeSell(Customer customer, PortfolioItem portfolioItem,
                                                 StockTradeRequest request) {
        customer.setBalance(customer.balance() + request.totalPrice());
        portfolioItem.setQuantity(portfolioItem.quantity()- request.quantity());
        return saveAndBuildResponse(customer, portfolioItem, request);
    }

    private Mono<StockTradeResponse> saveAndBuildResponse(Customer customer, PortfolioItem portfolioItem, StockTradeRequest request) {
        var response = EntityDtoMapper.toStockTradeResponse(request, customer.id(), customer.balance());
        return Mono.zip(customerRepository.save(customer), portfolioItemRepository.save(portfolioItem))
                .thenReturn(response);
    }
}
