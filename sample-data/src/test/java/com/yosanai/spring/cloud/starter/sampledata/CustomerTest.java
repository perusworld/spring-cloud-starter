package com.yosanai.spring.cloud.starter.sampledata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = TestConfig.class)
public class CustomerTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Before
	public void init() {
	}

	@Test
	public void initCheck() {
		assertNotNull(customerRepository);
	}

	@Test
	public void checkInsert() {
		Customer customer = new Customer("firstName", "lastName");
		Customer savedCustomer = customerRepository.save(customer);
		assertNotNull(savedCustomer);
		assertTrue(null != savedCustomer.getId());
	}

	@Test
	public void checkFindByLastName() {
		String lastName = "lastName";
		customerRepository.save(new Customer("firstName1", lastName));
		customerRepository.save(new Customer("firstName2", lastName));
		customerRepository.save(new Customer("firstName3", lastName));
		customerRepository.save(new Customer("firstName4", lastName));
		customerRepository.save(new Customer("firstName5", lastName));
		
		List<Customer> customers = customerRepository.findByLastName(lastName);
		assertNotNull(customers);
		assertEquals(5,  customers.size());
		for (Customer customer : customers) {
			assertEquals(lastName,  customer.getLastName());
		}

	}
}
