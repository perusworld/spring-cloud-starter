package com.yosanai.spring.cloud.starter.sampledata.repository;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonView;
import com.yosanai.spring.cloud.starter.sampledata.Views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonView(Views.Public.class)
public class OrderSummary {

	private Date orderDate;
	private long salesAmount;
	private long salesCount;

}
