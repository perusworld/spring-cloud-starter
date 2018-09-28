package com.yosanai.spring.cloud.starter.samplerestservice.jpa.integration;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yosanai.spring.cloud.starter.sampledata.model.Customer;
import com.yosanai.spring.cloud.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.cloud.starter.sampledata.model.OrderItem;
import com.yosanai.spring.cloud.starter.sampledata.model.Product;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.CustomerController;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.CustomerOrderController;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.ProductController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@Transactional
@ActiveProfiles("test")
public class BaseControllerTest {

	public static final int BATCH_SIZE = 5;

	@LocalServerPort
	protected int port;

	@Value("${local.management.port}")
	private int mgmtPort;

	@Autowired
	protected TestRestTemplate restTemplate;

	protected String getURL(Class<?> classDef, String... path) {
		return String.format("http://localhost:%d/%s/%s", port, classDef.getAnnotation(RequestMapping.class).value()[0],
				null == path ? "" : Stream.of(path).collect(Collectors.joining("/")));
	}

	protected String getMgmtURL(String suffix) {
		return String.format("http://localhost:%d/%s", mgmtPort, suffix);
	}

	protected String encodeAsParams(Object... params) throws UnsupportedEncodingException {
		StringBuilder ret = new StringBuilder();
		if (null != params) {
			for (int idx = 0; idx < params.length; idx = idx + 2) {
				if (0 < ret.length()) {
					ret.append("&");
				}
				ret.append(params[idx]).append("=")
						.append(URLEncoder.encode(params[idx + 1].toString(), StandardCharsets.UTF_8.toString()));
			}
		}
		return ret.toString();
	}

	protected String getURLWithParam(String url, Object... params) throws UnsupportedEncodingException {
		return StringUtils.joinWith("?", url, encodeAsParams(params));
	}

	public int rndInt() {
		return RandomUtils.nextInt(1, 1000);
	}

	public String rndStr() {
		return rndStr(10);
	}

	public String rndStr(int size) {
		return RandomStringUtils.random(size, true, true);
	}

	public Customer someCustomer() {
		return restTemplate.postForObject(getURL(CustomerController.class, ""),
				new HttpEntity<>(new Customer(rndStr(), rndStr(), rndStr())), Customer.class);
	}

	public CustomerOrder someCustomerOrder(Customer customer) {
		return restTemplate.postForObject(getURL(CustomerOrderController.class, ""),
				new HttpEntity<>(new CustomerOrder(customer)), CustomerOrder.class);
	}

	public CustomerOrder addOrderItem(CustomerOrder order, OrderItem item) {
		return restTemplate.postForObject(getURL(CustomerOrderController.class, order.getId().toString(), "order-item"),
				new HttpEntity<>(item), CustomerOrder.class);
	}

	public List<Product> someProducts(int size) {
		List<Product> ret = new ArrayList<>();
		for (int idx = 0; idx < BATCH_SIZE; idx++) {
			ret.add(restTemplate.postForObject(getURL(ProductController.class, ""),
					new HttpEntity<>(new Product(rndStr(), rndStr(), rndInt())), Product.class));
		}
		return ret;
	}

	public OrderItem someOrderItem(Product product) {
		return new OrderItem(product, rndInt());
	}

	public List<CustomerOrder> someCustomerOrdersWithItems(Customer customer, int size) {
		List<CustomerOrder> ret = new ArrayList<>();
		List<Product> products = someProducts(size);
		for (int idx = 0; idx < size; idx++) {
			CustomerOrder order = someCustomerOrder(customer);
			for (int pIdx = 0; pIdx < RandomUtils.nextInt(1, products.size()); pIdx++) {
				order = addOrderItem(order, someOrderItem(products.get(pIdx)));
			}
			ret.add(order);
		}
		return ret;
	}

	@Test
	public void testContextLoads() throws Exception {
		assertTrue(0 < mgmtPort);
	}
}
