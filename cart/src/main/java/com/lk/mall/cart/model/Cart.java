package com.lk.mall.cart.model;

import java.util.List;

import javax.persistence.Embedded;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Cart {
	@Embedded
	private List<Shop> shopList;
	
}
