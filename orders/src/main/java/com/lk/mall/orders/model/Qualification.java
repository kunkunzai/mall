package com.lk.mall.orders.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Qualification {
	
	private Integer status;
	private String productId;
	private String userId;
	
}
