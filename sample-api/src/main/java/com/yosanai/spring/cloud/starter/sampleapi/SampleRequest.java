package com.yosanai.spring.cloud.starter.sampleapi;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SampleRequest {

	private String aString;
	private Integer anInteger;
	private Date aDate;

}
