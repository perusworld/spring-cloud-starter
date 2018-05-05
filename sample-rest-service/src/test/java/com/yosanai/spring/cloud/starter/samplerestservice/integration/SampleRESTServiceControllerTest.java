package com.yosanai.spring.cloud.starter.samplerestservice.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.yosanai.spring.cloud.starter.sampleapi.SampleRequest;
import com.yosanai.spring.cloud.starter.sampleapi.SampleResponse;
import com.yosanai.spring.cloud.starter.samplerestservice.SampleRESTServiceController;

import lombok.extern.java.Log;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@Log
public class SampleRESTServiceControllerTest {

	@Autowired
	private SampleRESTServiceController controller;

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

	protected String getRndString() {
		return RandomStringUtils.random(10, true, true);
	}

	protected Integer getRndNumber() {
		return Integer.parseInt(RandomStringUtils.random(5, false, true));
	}

	@Test
	public void testContextLoads() throws Exception {
		assertNotNull(controller);
	}

	@Test
	public void testIndexPage() throws Exception {
		assertEquals(restTemplate.getForObject(getURL(""), String.class), "Sample REST Service");
	}

	@Test
	public void testCallSampleAPI() throws Exception {
		SampleRequest request = new SampleRequest(getRndString(), getRndNumber(), new Date());
		SampleResponse response = restTemplate.postForObject(getURL("sample-api"), new HttpEntity<>(request),
				SampleResponse.class);
		assertNotNull(response);
		assertEquals(request.getADate(), response.getADate());
		assertEquals(request.getAnInteger(), response.getAnInteger());
		assertEquals(request.getAString(), response.getAString());
		log.info(response.toString());
	}

	@Test
	public void testHealthCheck() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = restTemplate.getForEntity(getMgmtURL("actuator/health"), Map.class);
		assertEquals(entity.getStatusCode(), HttpStatus.OK);
	}

}
