package com.sydragon.customerportfolio;

import com.sydragon.customerportfolio.domain.Ticker;
import com.sydragon.customerportfolio.domain.TradeAction;
import com.sydragon.customerportfolio.dto.StockTradeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@SpringBootTest
@AutoConfigureWebTestClient
class CustomerServiceApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(CustomerServiceApplicationTests.class);

	@Autowired
	private WebTestClient client;

	@Test
	public void customerInformation() {
		getCustomer(1, HttpStatus.OK)
				.jsonPath("$.name").isEqualTo("Sam")
				.jsonPath("$.balance").isEqualTo("10000")
				.jsonPath("$.holdings").isEmpty();
	}

	@Test
	public void buyAndSell() {
		//buy
		var buyRequest1 = new StockTradeRequest(Ticker.GOOGLE, 100, 5, TradeAction.BUY);
		trade(2, HttpStatus.OK, buyRequest1)
				.jsonPath("$.balance").isEqualTo(9500)
				.jsonPath("$.totalPrice").isEqualTo(500);

		var buyRequest2 = new StockTradeRequest(Ticker.GOOGLE, 100, 10, TradeAction.BUY);
		trade(2, HttpStatus.OK, buyRequest2)
				.jsonPath("$.balance").isEqualTo(8500)
				.jsonPath("$.totalPrice").isEqualTo(1000);

		//check the holdings
		getCustomer(2, HttpStatus.OK)
				.jsonPath("$.name").isEqualTo("Mike")
				.jsonPath("$.balance").isEqualTo("8500")
				.jsonPath("$.holdings").isNotEmpty()
				.jsonPath("$.holdings.length()").isEqualTo(1)
				.jsonPath("$.holdings[0].ticker").isEqualTo("GOOGLE")
				.jsonPath("$.holdings[0].quantity").isEqualTo(15);

		var sellRequest1 = new StockTradeRequest(Ticker.GOOGLE, 110, 5, TradeAction.SELL);
		trade(2, HttpStatus.OK, sellRequest1)
				.jsonPath("$.balance").isEqualTo(9050)
				.jsonPath("$.totalPrice").isEqualTo(550);

		var sellRequest2 = new StockTradeRequest(Ticker.GOOGLE, 110, 10, TradeAction.SELL);
		trade(2, HttpStatus.OK, sellRequest2)
				.jsonPath("$.balance").isEqualTo(10150)
				.jsonPath("$.totalPrice").isEqualTo(1100);

		//check the holdings
		getCustomer(2, HttpStatus.OK)
				.jsonPath("$.name").isEqualTo("Mike")
				.jsonPath("$.balance").isEqualTo("10150")
				.jsonPath("$.holdings").isNotEmpty()
				.jsonPath("$.holdings.length()").isEqualTo(1)
				.jsonPath("$.holdings[0].ticker").isEqualTo("GOOGLE")
				.jsonPath("$.holdings[0].quantity").isEqualTo(0);
	}

	@Test
	public void customerNotFound() {
		getCustomer(10, HttpStatus.NOT_FOUND)
				.jsonPath("$.detail").isEqualTo("Customer [id=10] is not found");

		var sellRequest = new StockTradeRequest(Ticker.GOOGLE, 110, 5, TradeAction.SELL);
		trade(10, HttpStatus.NOT_FOUND, sellRequest)
				.jsonPath("$.detail").isEqualTo("Customer [id=10] is not found");

	}

	@Test
	public void insufficientBalance() {
		var buyRequest = new StockTradeRequest(Ticker.GOOGLE, 100, 101, TradeAction.BUY);
		trade(3, HttpStatus.BAD_REQUEST, buyRequest)
				.jsonPath("$.detail").isEqualTo("Customer [id=3] does not have enough funds to complete the transaction");
	}

	@Test
	public void insufficientShares() {
		var sellRequest = new StockTradeRequest(Ticker.GOOGLE, 110, 1, TradeAction.SELL);
		trade(2, HttpStatus.BAD_REQUEST, sellRequest)
				.jsonPath("$.detail").isEqualTo("Customer [id=2] does not have enough shares to complete the transaction");
	}

	private WebTestClient.BodyContentSpec getCustomer(Integer customerId, HttpStatus expectedStatus) {
		return client.get()
				.uri("/customers/{customerId}", customerId)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody()
				.consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
	}

	private WebTestClient.BodyContentSpec trade(Integer customerId, HttpStatus expectedStatus, StockTradeRequest request) {
		return client.post()
				.uri("/customers/{customerId}/trade", customerId)
				.bodyValue(request)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody()
				.consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
	}
}
