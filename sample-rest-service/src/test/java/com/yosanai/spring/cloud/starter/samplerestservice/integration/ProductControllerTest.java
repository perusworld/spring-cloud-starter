package com.yosanai.spring.cloud.starter.samplerestservice.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.yosanai.spring.cloud.starter.sampledata.model.Product;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.ProductController;

import lombok.extern.java.Log;

@Log
public class ProductControllerTest extends BaseControllerTest {

	@Test
	public void testCreateProduct() throws Exception {
		Product product = new Product(rndStr(), rndStr(), rndInt());
		Product savedProduct = restTemplate.postForObject(getURL(ProductController.class, ""),
				new HttpEntity<>(product), Product.class);
		assertNotNull(savedProduct);
		log.info(savedProduct.toString());
		assertEquals(product.getName(), savedProduct.getName());
		Product getProduct = restTemplate.getForObject(getURL(ProductController.class, savedProduct.getId().toString()),
				Product.class);
		assertNotNull(getProduct);
		log.info(getProduct.toString());
		assertEquals(getProduct.getId(), savedProduct.getId());
		assertEquals(getProduct.getDescription(), savedProduct.getDescription());
	}

	@Test
	public void testFindAll() throws Exception {
		Map<Long, Product> productMap = new HashMap<>();
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			Product product = restTemplate.postForObject(getURL(ProductController.class, ""),
					new HttpEntity<>(new Product(rndStr(), rndStr(), rndInt())), Product.class);
			productMap.put(product.getId(), product);
		}
		ResponseEntity<List<Product>> resp = restTemplate.exchange(getURL(ProductController.class, ""), HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Product>>() {
				});
		assertNotNull(resp);
		assertNotNull(resp.getBody());
		List<Product> saved = resp.getBody();
		assertTrue(BATCH_SIZE <= saved.size());
		final AtomicInteger count = new AtomicInteger(0);
		saved.stream().filter(obj -> productMap.containsKey(obj.getId())).forEach(obj -> {
			count.incrementAndGet();
			assertTrue(productMap.get(obj.getId()).getName().equals(obj.getName()));
		});
		assertTrue(productMap.size() <= count.get());
	}

	@Test
	public void testFindByName() throws Exception {
		String lastName = rndStr();
		List<Product> products = new ArrayList<>();
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			products.add(restTemplate.postForObject(getURL(ProductController.class, ""),
					new HttpEntity<>(new Product(rndStr(), lastName, rndInt())), Product.class));
		}
		Product getProduct = restTemplate.getForObject(
				getURL(ProductController.class, "search", "findByName", products.get(0).getName()), Product.class);
		assertNotNull(getProduct);
		log.info(getProduct.toString());
		assertEquals(getProduct.getId(), products.get(0).getId());
		assertEquals(getProduct.getDescription(), products.get(0).getDescription());
	}
}
