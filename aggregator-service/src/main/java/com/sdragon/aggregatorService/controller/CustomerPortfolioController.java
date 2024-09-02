package com.sdragon.aggregatorService.controller;

import com.sdragon.aggregatorService.dto.CustomerInformation;
import com.sdragon.aggregatorService.dto.StockTradeResponse;
import com.sdragon.aggregatorService.dto.TradeRequest;
import com.sdragon.aggregatorService.service.CustomerPortfolioService;
import com.sdragon.aggregatorService.validator.RequestValidator;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
public class CustomerPortfolioController {

    private final CustomerPortfolioService customerPortfolioService;

    public CustomerPortfolioController(CustomerPortfolioService customerPortfolioService) {
        this.customerPortfolioService = customerPortfolioService;
    }

    @GetMapping("/{customerId}")
    public Mono<CustomerInformation> getCustomerInformation(@PathVariable Integer customerId) {
        return customerPortfolioService.getCustomerInformation(customerId);
    }

    @PostMapping("/{customerId}/trade")
    public Mono<StockTradeResponse> trade(@PathVariable Integer customerId,
                                          @RequestBody Mono<TradeRequest> mono) {
        return mono.transform(RequestValidator.validate())
                .flatMap(request -> customerPortfolioService.trade(customerId, request));
    }
}
