package com.yosanai.spring.cloud.starter.sampleamqpservice;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import com.yosanai.spring.cloud.starter.sampleapi.SampleAPI;

@EnableDiscoveryClient
@EnableEurekaClient
@SpringBootApplication
public class SampleAMQPServiceApplication {

	@Value("${sample.amqpservice.topicExchange}")
	private String topicExchange;

	@Value("${sample.amqpservice.queue}")
	private String queue;

	@Value("${sample.amqpservice.routingKey}")
	private String routingKey;

	@Bean
	public SampleAPI sampleAPI() {
		return new SampleAPI();
	}

	@Bean
	Queue queue() {
		return new Queue(queue, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange(topicExchange);
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(routingKey);
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queue);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(SampleRequestListener receiver) {
		return new MessageListenerAdapter(receiver, "handleMessage");
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleAMQPServiceApplication.class, args);
	}

}
