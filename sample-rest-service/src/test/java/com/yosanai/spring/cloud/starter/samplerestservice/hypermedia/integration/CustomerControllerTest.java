package com.yosanai.spring.cloud.starter.samplerestservice.hypermedia.integration;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.yosanai.spring.cloud.starter.sampledata.model.Customer;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.CustomerController;

import lombok.extern.java.Log;

@Log
public class CustomerControllerTest extends BaseControllerTest {

	@Test
	public void testCreateCustomer() throws Exception {
		Customer customer = new Customer(rndStr(), rndStr(), rndStr());
		Customer savedCustomer = post(CustomerController.class, customer, Customer.class);
		assertNotNull(savedCustomer);
		log.info(savedCustomer.toString());
		assertEquals(customer.getFirstName(), savedCustomer.getFirstName());
		Customer getCustomer = findBy(CustomerController.class, Customer.class, savedCustomer.getId().toString());
		assertNotNull(getCustomer);
		log.info(getCustomer.toString());
		assertEquals(getCustomer.getId(), savedCustomer.getId());
		assertEquals(getCustomer.getLastName(), savedCustomer.getLastName());
	}

	@Test
	public void testFindAll() throws Exception {

		Map<Long, Customer> customerMap = new HashMap<>();
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			Customer customer = post(CustomerController.class, new Customer(rndStr(), rndStr(), rndStr()),
					Customer.class);
			customerMap.put(customer.getId(), customer);
		}
		List<Customer> saved = findAllBy(CustomerController.class, Customer.class);
		assertNotNull(saved);
		assertTrue(BATCH_SIZE <= saved.size());
		final AtomicInteger count = new AtomicInteger(0);
		saved.stream().filter(obj -> customerMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(customerMap.get(obj.getId()).getLastName().equals(obj.getLastName()));
		});
		assertTrue(customerMap.size() <= count.get());
	}

	@Test
	public void testFindAllByLastName() throws Exception {
		Map<Long, Customer> customerMap = new HashMap<>();
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			Customer customer = post(CustomerController.class, new Customer(rndStr(), rndStr(), rndStr()),
					Customer.class);
			customerMap.put(customer.getId(), customer);
		}
		List<Customer> matched = findAllBy(CustomerController.class, Customer.class, getURLWithParam(
				"search/findAllByLastName", "lastName", customerMap.values().iterator().next().getLastName()));
		assertNotNull(matched);
		assertTrue(1 == matched.size());
		final AtomicInteger count = new AtomicInteger(0);
		matched.stream().filter(obj -> customerMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(customerMap.get(obj.getId()).getLastName().equals(obj.getLastName()));
		});
		assertEquals(1, count.get());
	}
}
