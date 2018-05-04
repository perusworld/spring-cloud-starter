package com.yosanai.spring.cloud.starter.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ServcieRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServcieRegistryApplication.class, args);
	}

}
