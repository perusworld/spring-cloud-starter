package com.yosanai.spring.cloud.starter.sampledata.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.yosanai.spring.cloud.starter.sampledata.model.Product;

@RepositoryRestResource
public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

	public Product findByName(String name);

}