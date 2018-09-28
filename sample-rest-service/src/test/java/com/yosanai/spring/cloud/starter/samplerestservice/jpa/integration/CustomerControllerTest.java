package com.yosanai.spring.cloud.starter.samplerestservice.jpa.integration;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.yosanai.spring.cloud.starter.sampledata.model.Customer;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.CustomerController;

import lombok.extern.java.Log;

@Log
public class CustomerControllerTest extends BaseControllerTest {

	@Test
	public void testCreateCustomer() throws Exception {
		Customer customer = new Customer(rndStr(), rndStr(), rndStr());
		Customer savedCustomer = restTemplate.postForObject(getURL(CustomerController.class, ""),
				new HttpEntity<>(customer), Customer.class);
		assertNotNull(savedCustomer);
		assertNotNull(customer.getSampleIgnoreInPublic());
		assertNull(savedCustomer.getSampleIgnoreInPublic());
		log.info(savedCustomer.toString());
		assertEquals(customer.getFirstName(), savedCustomer.getFirstName());
		Customer getCustomer = restTemplate
				.getForObject(getURL(CustomerController.class, savedCustomer.getId().toString()), Customer.class);
		assertNotNull(getCustomer);
		assertNull(savedCustomer.getSampleIgnoreInPublic());
		log.info(getCustomer.toString());
		assertEquals(getCustomer.getId(), savedCustomer.getId());
		assertEquals(getCustomer.getLastName(), savedCustomer.getLastName());
	}

	@Test
	public void testFindAll() throws Exception {
		Map<Long, Customer> customerMap = new HashMap<>();
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			Customer customer = restTemplate.postForObject(getURL(CustomerController.class, ""),
					new HttpEntity<>(new Customer(rndStr(), rndStr(), rndStr())), Customer.class);
			customerMap.put(customer.getId(), customer);
		}
		ResponseEntity<List<Customer>> resp = restTemplate.exchange(getURL(CustomerController.class, ""),
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Customer>>() {
				});
		assertNotNull(resp);
		assertNotNull(resp.getBody());
		List<Customer> saved = resp.getBody();
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
		String lastName = rndStr();
		Map<Long, Customer> customerMap = new HashMap<>();
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			Customer customer = restTemplate.postForObject(getURL(CustomerController.class, ""),
					new HttpEntity<>(new Customer(rndStr(), lastName, rndStr())), Customer.class);
			customerMap.put(customer.getId(), customer);
		}
		ResponseEntity<List<Customer>> resp = restTemplate.exchange(
				getURL(CustomerController.class, "search", "findAllByLastName", lastName), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Customer>>() {
				});
		assertNotNull(resp);
		assertNotNull(resp.getBody());
		List<Customer> saved = resp.getBody();
		assertTrue(BATCH_SIZE <= saved.size());
		final AtomicInteger count = new AtomicInteger(0);
		saved.stream().filter(obj -> customerMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(customerMap.get(obj.getId()).getLastName().equals(obj.getLastName()));
		});
		assertEquals(customerMap.size(), count.get());
	}
}
