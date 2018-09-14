package com.yosanai.spring.cloud.starter.sampledata.repository;

import java.sql.Date;

public interface OrderSummary {

	public Date getOrderDate();

	public int getSalesAmount();

	public int getSalesCount();

}
