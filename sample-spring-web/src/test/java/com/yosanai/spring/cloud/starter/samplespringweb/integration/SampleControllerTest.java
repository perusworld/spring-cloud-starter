package com.yosanai.spring.cloud.starter.samplespringweb.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.yosanai.spring.cloud.starter.samplespringweb.SampleController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
public class SampleControllerTest {
	@Autowired
	private SampleController controller;

	@LocalServerPort
	private int port;

	@Value("${local.management.port}")
	private int mgmtPort;

	@Autowired
	private TestRestTemplate restTemplate;

	protected String getURL(String suffix) {
		return String.format("http://localhost:%d/%s", port, suffix);
	}

	protected String getMgmtURL(String suffix) {
		return String.format("http://localhost:%d/%s", mgmtPort, suffix);
	}

	@Test
	public void testContextLoads() throws Exception {
		assertNotNull(controller);
	}

	@Test
	public void testIndexPage() throws Exception {
		assertTrue(restTemplate.getForObject(getURL(""), String.class).contains("Index - Sample Spring Web"));
	}

	@Test
	public void testHealthCheck() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = restTemplate.getForEntity(getMgmtURL("actuator/health"), Map.class);
		assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

}
