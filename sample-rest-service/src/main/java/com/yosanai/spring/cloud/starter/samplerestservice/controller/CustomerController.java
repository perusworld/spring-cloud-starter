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
import com.yosanai.spring.cloud.starter.sampledata.model.Customer;
import com.yosanai.spring.cloud.starter.sampledata.projection.CustomerRepository;
import com.yosanai.spring.cloud.starter.samplerestservice.ResourceException;

@RestController
@RequestMapping("/customer/")
public class CustomerController {

	@Autowired
	private CustomerRepository repository;

	@GetMapping("/")
	@JsonView(Views.Public.class)
	public List<Customer> list(@NotNull final Pageable pageable) {
		return repository.findAll(pageable).getContent();
	}

	@PostMapping("/")
	@JsonView(Views.Public.class)
	public Customer save(@Valid @RequestBody Customer customer) {
		return repository.save(customer);
	}

	@GetMapping("/{id}")
	@JsonView(Views.Public.class)
	public Customer get(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceException(getClass().getSimpleName(), "id", id));
	}

	@GetMapping("/search/findAllByLastName/{lastName}")
	@JsonView(Views.Public.class)
	public List<Customer> getByLastName(@PathVariable String lastName) {
		return repository.findAllByLastName(lastName);
	}

}
