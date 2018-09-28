package com.yosanai.spring.cloud.starter.sampledata.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.yosanai.spring.cloud.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.cloud.starter.sampledata.model.OrderItem;

@RepositoryRestResource(path = "order-items")
public interface OrderItemRepository extends PagingAndSortingRepository<OrderItem, Long> {

	public List<OrderItem> findAllByCustomerOrder(CustomerOrder customerOrder);

	public List<OrderItem> findAllByCustomerOrderId(@Param("customerOrderId") Long customerOrderId);

}
