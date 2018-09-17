package com.yosanai.spring.cloud.starter.samplerestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.yosanai.spring.cloud.starter.sampleapi.SampleAPI;
import com.yosanai.spring.cloud.starter.sampledata.model.Auditable;
import com.yosanai.spring.cloud.starter.sampledata.projection.CustomerRepository;

@EnableDiscoveryClient
@EnableEurekaClient
@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackageClasses = { Auditable.class })
@EnableJpaRepositories(basePackageClasses = { CustomerRepository.class })
@ComponentScan(basePackages = { "com.yosanai" })
public class SampleRESTServiceApplication {

	@Bean
	public SampleAPI sampleAPI() {
		return new SampleAPI();
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleRESTServiceApplication.class, args);
	}

}
