package com.yosanai.spring.cloud.starter.samplerestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.yosanai.spring.cloud.starter.sampleapi.SampleAPI;

@EnableDiscoveryClient
@SpringBootApplication
public class SampleRESTServiceApplication {

	@Bean
	public SampleAPI sampleAPI() {
		return new SampleAPI();
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleRESTServiceApplication.class, args);
	}

}
