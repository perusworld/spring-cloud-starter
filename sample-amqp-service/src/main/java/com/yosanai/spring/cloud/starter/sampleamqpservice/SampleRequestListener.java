package com.yosanai.spring.cloud.starter.sampleamqpservice;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yosanai.spring.cloud.starter.sampleapi.SampleAPI;
import com.yosanai.spring.cloud.starter.sampleapi.SampleRequest;

import lombok.extern.java.Log;

@Log
@Component
public class SampleRequestListener {

	private CountDownLatch latch = new CountDownLatch(1);

	@Autowired
	private SampleAPI sampleAPI;

	public void handleMessage(SampleRequest request) {
		log.info("Got message " + request.toString());
		latch.countDown();
	}

	public CountDownLatch getLatch() {
		return latch;
	}

}
