package com.yosanai.spring.cloud.starter.samplerestservice.jpa.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.yosanai.spring.cloud.starter.sampledata.model.Customer;
import com.yosanai.spring.cloud.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.cloud.starter.sampledata.projection.OrderSummary;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.CustomerOrderController;

import lombok.extern.java.Log;

@Log
public class CustomerOrderControllerTest extends BaseControllerTest {

	@Test
	public void testCreateCustomerOrder() throws Exception {
		Customer customer = someCustomer();
		CustomerOrder savedCustomerOrder = someCustomerOrder(customer);
		assertNotNull(savedCustomerOrder);
		log.info(savedCustomerOrder.toString());
		assertEquals(customer.getId(), savedCustomerOrder.getCustomer().getId());
		CustomerOrder getCustomerOrder = restTemplate.getForObject(
				getURL(CustomerOrderController.class, savedCustomerOrder.getId().toString()), CustomerOrder.class);
		assertNotNull(getCustomerOrder);
		log.info(getCustomerOrder.toString());
		assertEquals(getCustomerOrder.getId(), savedCustomerOrder.getId());
		assertEquals(customer.getId(), getCustomerOrder.getCustomer().getId());
	}

	@Test
	public void testFindAll() throws Exception {
		Customer customer = someCustomer();
		Map<Long, CustomerOrder> customerOrderMap = new HashMap<>();
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			CustomerOrder customerOrder = someCustomerOrder(customer);
			customerOrderMap.put(customerOrder.getId(), customerOrder);
		}
		ResponseEntity<List<CustomerOrder>> resp = restTemplate.exchange(getURL(CustomerOrderController.class, ""),
				HttpMethod.GET, null, new ParameterizedTypeReference<List<CustomerOrder>>() {
				});
		assertNotNull(resp);
		assertNotNull(resp.getBody());
		List<CustomerOrder> saved = resp.getBody();
		assertTrue(BATCH_SIZE <= saved.size());
		final AtomicInteger count = new AtomicInteger(0);
		saved.stream().filter(obj -> customerOrderMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(customerOrderMap.get(obj.getId()).getCustomer().getId().equals(obj.getCustomer().getId()));
		});
		assertTrue(customerOrderMap.size() <= count.get());
	}

	@Test
	public void checkInsertWithItems() {
		Customer savedCustomer = someCustomer();
		List<CustomerOrder> orders = someCustomerOrdersWithItems(savedCustomer, BATCH_SIZE);
		assertNotNull(orders);
		assertEquals(BATCH_SIZE, orders.size());
		ResponseEntity<List<CustomerOrder>> resp = restTemplate.exchange(
				getURL(CustomerOrderController.class, "search", "findAllByCustomer", savedCustomer.getId().toString()),
				HttpMethod.GET, null, new ParameterizedTypeReference<List<CustomerOrder>>() {
				});
		assertNotNull(resp);
		assertNotNull(resp.getBody());
		List<CustomerOrder> savedOrders = resp.getBody();
		assertTrue(BATCH_SIZE <= savedOrders.size());
		assertNotNull(savedOrders);
		assertEquals(BATCH_SIZE, savedOrders.size());
		savedOrders.forEach(order -> {
			final AtomicInteger total = new AtomicInteger(0);
			order.getOrderItems().forEach(item -> {
				assertNotNull(item.getProduct());
				total.addAndGet(item.getQuantity() * item.getProduct().getCost());
			});
			assertEquals(order.getTotalCost(), total.get());
		});
	}

	@Test
	public void checkOrderSummary() {
		Customer savedCustomer = someCustomer();
		List<CustomerOrder> orders = someCustomerOrdersWithItems(savedCustomer, BATCH_SIZE);
		assertNotNull(orders);
		assertEquals(BATCH_SIZE, orders.size());
		ResponseEntity<List<OrderSummary>> resp = restTemplate.exchange(
				getURL(CustomerOrderController.class, savedCustomer.getId().toString(), "order-summary"),
				HttpMethod.GET, null, new ParameterizedTypeReference<List<OrderSummary>>() {
				});
		assertNotNull(resp);
		List<OrderSummary> summaries = resp.getBody();
		assertNotNull(summaries);
		summaries.forEach(summary -> {
			log.info(String.format("%s, %d, %d", summary.getOrderDate(), summary.getSalesAmount(),
					summary.getSalesCount()));
			assertTrue(0 < summary.getSalesCount());
			assertTrue(0 < summary.getSalesAmount());
		});
	}
}
