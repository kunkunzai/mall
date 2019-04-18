package com.lk.mall.product.model.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShopVO {
	private Long id;
	private Integer type;
	private String name;
	private String logo;
	private String description;
	private Integer status;

}
