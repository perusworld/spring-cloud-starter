package com.yosanai.spring.cloud.starter.samplerestservice.hypermedia.integration;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.yosanai.spring.cloud.starter.sampledata.model.Customer;
import com.yosanai.spring.cloud.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.CustomerController;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.CustomerOrderController;

import lombok.extern.java.Log;

@Log
public class CustomerOrderControllerTest extends BaseControllerTest {

	@Test
	public void testCreateCustomerOrder() throws Exception {
		Customer customer = post(CustomerController.class, new Customer(rndStr(), rndStr(), rndStr()), Customer.class);
		CustomerOrder savedCustomerOrder = post(CustomerOrderController.class, new CustomerOrder(customer),
				CustomerOrder.class);
		assertNotNull(savedCustomerOrder);
		assertNotNull(savedCustomerOrder.getId());
		log.info(savedCustomerOrder.toString());
		assertEquals(customer.getId(), savedCustomerOrder.getCustomer().getId());
		CustomerOrder getCustomerOrder = findBy(CustomerOrderController.class, CustomerOrder.class,
				savedCustomerOrder.getId().toString());
		assertNotNull(getCustomerOrder);
		log.info(getCustomerOrder.toString());
		assertEquals(getCustomerOrder.getId(), savedCustomerOrder.getId());
		assertEquals(customer.getId(), getCustomerOrder.getCustomer().getId());
	}

	@Test
	public void testFindAll() throws Exception {

		Map<Long, CustomerOrder> customerOrderMap = new HashMap<>();
		Customer customer = post(CustomerController.class, new Customer(rndStr(), rndStr(), rndStr()), Customer.class);
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			CustomerOrder customerOrder = post(CustomerOrderController.class, new CustomerOrder(customer),
					CustomerOrder.class);
			customerOrderMap.put(customerOrder.getId(), customerOrder);
		}
		List<CustomerOrder> saved = findAllBy(CustomerOrderController.class, CustomerOrder.class);
		assertNotNull(saved);
		assertTrue(BATCH_SIZE <= saved.size());
		final AtomicInteger count = new AtomicInteger(0);
		saved.stream().filter(obj -> customerOrderMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(customerOrderMap.get(obj.getId()).getCustomer().getId().equals(obj.getCustomer().getId()));
		});
		assertTrue(customerOrderMap.size() <= count.get());
	}

	@Test
	public void testFindAllByCustomer() throws Exception {
		Map<Long, CustomerOrder> customerOrderMap = new HashMap<>();
		Customer customer = post(CustomerController.class, new Customer(rndStr(), rndStr(), rndStr()), Customer.class);
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			CustomerOrder customerOrder = post(CustomerOrderController.class, new CustomerOrder(customer),
					CustomerOrder.class);
			customerOrderMap.put(customerOrder.getId(), customerOrder);
		}
		List<CustomerOrder> saved = findAllBy(CustomerOrderController.class, CustomerOrder.class,
				getURLWithParam("search/findAllByCustomerId", "customerId", customer.getId()));
		assertNotNull(saved);
		assertTrue(BATCH_SIZE <= saved.size());
		final AtomicInteger count = new AtomicInteger(0);
		saved.stream().filter(obj -> customerOrderMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(customerOrderMap.get(obj.getId()).getCustomer().getId().equals(obj.getCustomer().getId()));
		});
		assertTrue(customerOrderMap.size() <= count.get());
	}

}