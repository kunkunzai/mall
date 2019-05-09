package com.lk.mall.product.model.vo;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GoodsVO {

    private Long id;
    private Long productId;
    private BigDecimal price;
    private Integer stock;
    private String name;
    private String description;
    private String code;
    private Long shopId;
    private String shopName;
    private Integer status;
    private String image;
}
