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
import com.yosanai.spring.cloud.starter.sampledata.model.Product;
import com.yosanai.spring.cloud.starter.sampledata.repository.ProductRepository;
import com.yosanai.spring.cloud.starter.samplerestservice.ResourceException;

@RestController
@RequestMapping("jpa/products")
public class ProductController {

	@Autowired
	private ProductRepository repository;

	@GetMapping
	@JsonView(Views.Public.class)
	public List<Product> list(@NotNull final Pageable pageable) {
		return repository.findAll(pageable).getContent();
	}

	@PostMapping
	@JsonView(Views.Public.class)
	public Product save(@Valid @RequestBody Product product) {
		return repository.save(product);
	}

	@GetMapping("{id}")
	@JsonView(Views.Public.class)
	public Product get(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceException(getClass().getSimpleName(), "id", id));
	}

	@GetMapping("search/findByName/{name}")
	@JsonView(Views.Public.class)
	public Product getByLastName(@PathVariable String name) {
		return repository.findByName(name);
	}

}
