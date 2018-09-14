package com.yosanai.spring.cloud.starter.sampledata.projection;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.yosanai.spring.cloud.starter.sampledata.model.Customer;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

	public List<Customer> findAllByLastName(String lastName);

}
