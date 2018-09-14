package com.yosanai.spring.cloud.starter.sampledata.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonView;
import com.yosanai.spring.cloud.starter.sampledata.Views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@JsonView(Views.Public.class)
public class CustomerOrder extends Auditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Customer customer;

	@OneToMany(mappedBy = "customerOrder", orphanRemoval = true, cascade = CascadeType.ALL)
	private Set<OrderItem> items;

	private int totalCost;

	public void addItem(OrderItem item) {
		if (null == items) {
			items = new HashSet<>();
		}
		items.add(item);
		item.setCustomerOrder(this);
	}

	public void removeItem(OrderItem item) {
		if (null != items) {
			items.remove(item);
		}
		item.setCustomerOrder(null);
	}

	public CustomerOrder(Customer customer) {
		super();
		this.customer = customer;
	}

	@PrePersist
	@PreUpdate
	public void calculateTotal() {
		totalCost = 0;
		if (null != items) {
			items.forEach(item -> {
				if (null != item.getProduct()) {
					totalCost += item.getProduct().getCost() * item.getQuantity();
				}
			});
		}
	}
}
