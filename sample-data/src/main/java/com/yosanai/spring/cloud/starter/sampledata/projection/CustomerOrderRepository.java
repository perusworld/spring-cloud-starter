package com.yosanai.spring.cloud.starter.sampledata.projection;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.yosanai.spring.cloud.starter.sampledata.model.Customer;
import com.yosanai.spring.cloud.starter.sampledata.model.CustomerOrder;
import com.yosanai.spring.cloud.starter.sampledata.repository.OrderSummary;

public interface CustomerOrderRepository extends PagingAndSortingRepository<CustomerOrder, Long> {
	public List<CustomerOrder> findAllByCustomer(Customer customer);

	public List<CustomerOrder> findAllByCustomerId(Long id);

	@Query("select new com.yosanai.spring.cloud.starter.sampledata.repository.OrderSummary(cast(co.created as date) as orderDate, sum(co.totalCost) as salesAmount, count(co.id) as salesCount)"
			+ " from CustomerOrder co, OrderItem oi where co.customer = :customer"
			+ " and oi.customerOrder = co group by cast(co.created as date) order by orderDate")
	public Iterable<OrderSummary> orderSummaryByDay(@Param("customer") Customer customer);

	@Query("select new com.yosanai.spring.cloud.starter.sampledata.repository.OrderSummary(cast(co.created as date) as orderDate, sum(co.totalCost) as salesAmount, count(co.id) as salesCount)"
			+ " from CustomerOrder co, OrderItem oi where co.customer.id = :customerId"
			+ " and oi.customerOrder = co group by cast(co.created as date) order by orderDate")
	public List<OrderSummary> orderSummaryByDay(@Param("customerId") Long customerId);

}
