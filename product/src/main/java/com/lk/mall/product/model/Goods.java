package com.lk.mall.product.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "goods")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Goods {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message="商品id不能为空")
    private Long productId;
    @NotNull(message="价格不能为空")
    @DecimalMin(value = "0.01")
    private BigDecimal price;
    @NotNull(message="库存不能为空")
    private Integer stock;
    @Transient
    @Embedded
    @NotEmpty(message="sku属性值不能为空")
    @Valid
    private List<GoodsValue> goodsValueList;

    public List<GoodsValue> getGoodsValueList() {
        if (null == goodsValueList) {
            return new ArrayList<>();
        }
        return goodsValueList;
    }

}
