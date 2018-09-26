package com.yosanai.spring.cloud.starter.sampledata.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.yosanai.spring.cloud.starter.sampledata.model.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

	public Product findByName(String name);

}