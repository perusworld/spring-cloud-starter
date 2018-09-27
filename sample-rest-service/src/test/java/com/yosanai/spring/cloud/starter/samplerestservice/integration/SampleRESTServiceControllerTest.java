package com.yosanai.spring.cloud.starter.samplerestservice.integration;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.yosanai.spring.cloud.starter.sampleapi.SampleRequest;
import com.yosanai.spring.cloud.starter.sampleapi.SampleResponse;
import com.yosanai.spring.cloud.starter.samplerestservice.controller.SampleRESTServiceController;

import lombok.extern.java.Log;

@Log
public class SampleRESTServiceControllerTest extends BaseControllerTest {

	@Test
	public void testIndexPage() throws Exception {
		assertEquals(restTemplate.getForObject(getURL(SampleRESTServiceController.class, "index"), String.class),
				"Sample REST Service");
	}

	@Test
	public void testCallSampleAPI() throws Exception {
		SampleRequest request = new SampleRequest(rndStr(), rndInt(), new Date());
		SampleResponse response = restTemplate.postForObject(getURL(SampleRESTServiceController.class, "call-api"),
				new HttpEntity<>(request), SampleResponse.class);
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
