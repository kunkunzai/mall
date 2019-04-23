package com.lk.mall.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class ProductCart implements Serializable, Cloneable {

	private static final long serialVersionUID = -5510967080302695103L;

	@NotNull(message="店铺id不能为空")
	private Long productId;
	@NotNull(message="数量不能为空")
	private Integer quantity;
	private Boolean check;
	private String productName;
	private String productImage;
	private String subtitle;
	private BigDecimal productMoney;
	private Integer stock;
	
	 public ProductCart(Long productId, Integer quantity, Boolean check) {
        super();
        this.productId = productId;
        this.quantity = quantity;
        this.check = check;

    }
	
	
	

}
