package com.yosanai.spring.cloud.starter.samplespringweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy;

@EnableDiscoveryClient
@SpringBootApplication
public class SampleSpringWebApplication {

	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect(new GroupingStrategy());
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleSpringWebApplication.class, args);
	}

}
