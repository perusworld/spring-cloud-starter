package com.yosanai.spring.cloud.starter.samplerestservice.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.yosanai.spring.cloud.starter.sampledata.Views;
import com.yosanai.spring.cloud.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.cloud.starter.sampledata.model.OrderItem;
import com.yosanai.spring.cloud.starter.sampledata.projection.OrderSummary;
import com.yosanai.spring.cloud.starter.sampledata.repository.CustomerOrderRepository;
import com.yosanai.spring.cloud.starter.samplerestservice.ResourceException;

@RestController
@RequestMapping("jpa/customer-order")
public class CustomerOrderController {

	@Autowired
	private CustomerOrderRepository repository;

	@GetMapping
	@JsonView(Views.Public.class)
	public List<CustomerOrder> list(@NotNull final Pageable pageable) {
		return repository.findAll(pageable).getContent();
	}

	@PostMapping
	@JsonView(Views.Public.class)
	public CustomerOrder save(@Valid @RequestBody CustomerOrder customerOrder) {
		return repository.save(customerOrder);
	}

	@PostMapping("{id}/order-item")
	@JsonView(Views.Public.class)
	public CustomerOrder addOrderItem(@PathVariable Long id, @Valid @RequestBody OrderItem orderItem) {
		CustomerOrder ret = repository.findById(id)
				.orElseThrow(() -> new ResourceException(getClass().getSimpleName(), "id", id));
		ret.addOrderItem(orderItem);
		return repository.save(ret);
	}

	@GetMapping("{id}")
	@JsonView(Views.Public.class)
	public CustomerOrder get(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceException(getClass().getSimpleName(), "id", id));
	}

	@GetMapping("search/findAllByCustomer/{customerId}")
	@JsonView(Views.Public.class)
	public List<CustomerOrder> findByCustomer(@PathVariable Long customerId) {
		return repository.findAllByCustomerId(customerId);
	}

	@GetMapping("/{customerId}/order-summary")
	@JsonView(Views.Public.class)
	public List<OrderSummary> getOrderSummary(@NotNull @PathVariable Long customerId) {
		return repository.orderSummaryByDay(customerId);
	}

}
