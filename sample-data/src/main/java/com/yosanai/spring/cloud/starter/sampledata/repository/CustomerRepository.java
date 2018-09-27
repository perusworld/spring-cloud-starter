package com.yosanai.spring.cloud.starter.sampledata.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.yosanai.spring.cloud.starter.sampledata.model.Customer;

@RepositoryRestResource
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

	public List<Customer> findAllByLastName(String lastName);

}
