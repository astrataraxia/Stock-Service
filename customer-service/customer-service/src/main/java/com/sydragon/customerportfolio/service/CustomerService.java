package com.sydragon.customerportfolio.service;

import com.sydragon.customerportfolio.dto.CustomerInformation;
import com.sydragon.customerportfolio.entity.Customer;
import com.sydragon.customerportfolio.exceptions.ApplicationException;
import com.sydragon.customerportfolio.mapper.EntityDtoMapper;
import com.sydragon.customerportfolio.repository.CustomerRepository;
import com.sydragon.customerportfolio.repository.PortfolioItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public CustomerService(CustomerRepository customerRepository, PortfolioItemRepository portfolioItemRepository) {
        this.customerRepository = customerRepository;
        this.portfolioItemRepository = portfolioItemRepository;
    }

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationException.customerNotFound(customerId))
                .flatMap(this::buildCustomerInformation);
    }

    private Mono<CustomerInformation> buildCustomerInformation(Customer customer) {
        return portfolioItemRepository.findAllByCustomerId(customer.id())
                .collectList()
                .map(items -> EntityDtoMapper.toCustomerInformation(customer, items));
    }
}
