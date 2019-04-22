package com.lk.mall.cart.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class Check implements Serializable, Cloneable {

	private static final long serialVersionUID = -5510967080302695103L;

	@NotNull(message="选中&取消选中状态不能为空")
	private Boolean checkStatus;
	
	private Boolean checkAll;
	private Long shopId;
	private Long productId;
	
}
