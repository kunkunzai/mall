package com.lk.mall.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class ProductCart implements Serializable, Cloneable {

    private static final long serialVersionUID = -5510967080302695103L;

    private Long productId;
    private Integer quantity;
    private Integer status;
    private Boolean check;
    private String productName;
    private String productImage;
    private String subtitle;
    private BigDecimal productMoney;
    private BigDecimal totalMoney;
    private Integer stock;

    public ProductCart(Long productId, Integer quantity, Boolean check) {
        super();
        this.productId = productId;
        this.quantity = quantity;
        this.check = check;

    }

}