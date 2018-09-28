package com.yosanai.spring.cloud.starter.sampledata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestBody;

import com.yosanai.spring.cloud.starter.sampledata.model.Customer;
import com.yosanai.spring.cloud.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.cloud.starter.sampledata.projection.OrderSummary;

@RepositoryRestResource(path = "customer-orders")
public interface CustomerOrderRepository extends PagingAndSortingRepository<CustomerOrder, Long> {
	public List<CustomerOrder> findAllByCustomer(@RequestBody Customer customer);

	public List<CustomerOrder> findAllByCustomerId(@Param("customerId") Long customerId);

	@Query("select new com.yosanai.spring.cloud.starter.sampledata.projection.OrderSummary(cast(co.created as date) as orderDate, sum(co.totalCost) as salesAmount, count(co.id) as salesCount)"
			+ " from CustomerOrder co, OrderItem oi where co.customer = :customer"
			+ " and oi.customerOrder = co group by cast(co.created as date) order by orderDate")
	public Iterable<OrderSummary> summaryByDayForCustomer(Customer customer);

	@Query("select new com.yosanai.spring.cloud.starter.sampledata.projection.OrderSummary(cast(co.created as date) as orderDate, sum(co.totalCost) as salesAmount, count(co.id) as salesCount)"
			+ " from CustomerOrder co, OrderItem oi where co.customer.id = :customerId"
			+ " and oi.customerOrder = co group by cast(co.created as date) order by orderDate")
	public List<OrderSummary> summaryByDayForCustomerId(@Param("customerId") Long customerId);

}
