package com.yosanai.spring.cloud.starter.samplerestservice.hypermedia.integration;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.yosanai.spring.cloud.starter.sampledata.model.Product;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.ProductController;

import lombok.extern.java.Log;

@Log
public class ProductControllerTest extends BaseControllerTest {

	@Test
	public void testCreateProduct() throws Exception {
		Product product = new Product(rndStr(), rndStr(), rndInt());
		Product savedProduct = post(ProductController.class, product, Product.class);
		assertNotNull(savedProduct);
		log.info(savedProduct.toString());
		assertEquals(product.getName(), savedProduct.getName());
		Product getProduct = findBy(ProductController.class, Product.class, savedProduct.getId().toString());
		assertNotNull(getProduct);
		log.info(getProduct.toString());
		assertEquals(getProduct.getId(), savedProduct.getId());
		assertEquals(getProduct.getName(), savedProduct.getName());
	}

	@Test
	public void testFindAll() throws Exception {

		Map<Long, Product> productMap = new HashMap<>();
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			Product product = post(ProductController.class, new Product(rndStr(), rndStr(), rndInt()), Product.class);
			productMap.put(product.getId(), product);
		}
		List<Product> saved = findAllBy(ProductController.class, Product.class);
		assertNotNull(saved);
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
		Map<Long, Product> productMap = new HashMap<>();
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			Product product = post(ProductController.class, new Product(rndStr(), rndStr(), rndInt()), Product.class);
			productMap.put(product.getId(), product);
		}
		Product match = findBy(ProductController.class, Product.class,
				getURLWithParam("search/findByName", "name", productMap.values().iterator().next().getName()));
		assertNotNull(match);
		assertTrue(productMap.get(match.getId()).getName().equals(match.getName()));
	}
}
