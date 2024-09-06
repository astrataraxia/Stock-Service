package com.sdragon.aggregatorService.tests;

import com.sdragon.aggregatorService.domain.Ticker;
import com.sdragon.aggregatorService.domain.TradeAction;
import com.sdragon.aggregatorService.dto.TradeRequest;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.model.RegexBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

public class CustomerTradeTest extends AbstractIntegrationTest{

    private static final Logger log = LoggerFactory.getLogger(CustomerTradeTest.class);

    @Test
    public void tradeSuccess() {
        mockCustomerTrade("customer-service/customer-trade-200.json", 200);

        var tradeRequest = new TradeRequest(Ticker.GOOGLE, TradeAction.BUY, 2);
        postTrade(tradeRequest, HttpStatus.OK)
                .jsonPath("$.balance").isEqualTo(9780)
                .jsonPath("$.totalPrice").isEqualTo(220);
    }

    @Test
    public void tradeFailure() {
        mockCustomerTrade("customer-service/customer-trade-400.json", 400);

        var tradeRequest = new TradeRequest(Ticker.GOOGLE, TradeAction.BUY, 2);
        postTrade(tradeRequest, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Customer [id=1] does not have enough funds to complete the transaction");
    }

    @Test
    public void inputValidation() {
        var missingTicker = new TradeRequest(null, TradeAction.BUY, 2);
        postTrade(missingTicker, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Ticker is required");

        var missingAction = new TradeRequest(Ticker.GOOGLE, null, 2);
        postTrade(missingAction, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Trade action is required");

        var invalidQuantity = new TradeRequest(Ticker.GOOGLE, TradeAction.BUY, -2);
        postTrade(invalidQuantity, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Quantity should be more then 0");
    }

    private void mockCustomerTrade(String path, int responseCode) {
        // mock stock-service price response
        var stockResponseBody = resourceToString("stock-service/stock-price-200.json");
        mockServerClient
                .when(HttpRequest.request("/stock/GOOGLE"))
                .respond(HttpResponse.response(stockResponseBody)
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON));

        // mock customer-service trade response
        var customerResponseBody = resourceToString(path);
        mockServerClient
                .when(HttpRequest.request("/customers/1/trade")
                        .withMethod("POST")
                        .withBody(RegexBody.regex(".*\"price\":110.*")))
                .respond(HttpResponse.response(customerResponseBody)
                        .withStatusCode(responseCode)
                        .withContentType(MediaType.APPLICATION_JSON));
    }

    private WebTestClient.BodyContentSpec postTrade(TradeRequest tradeRequest, HttpStatus expectedStatus) {
        return client.post()
                .uri("/customers/{customerId}/trade", 1)
                .bodyValue(tradeRequest)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }
}
