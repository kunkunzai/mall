package com.lk.mall.product.model.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class CollectVO {

    private Long productId;
    private BigDecimal price;
    private String name;
    private Integer collectNum;
    
}
