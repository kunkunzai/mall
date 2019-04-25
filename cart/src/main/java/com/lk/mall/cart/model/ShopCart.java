package com.lk.mall.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class ShopCart implements Serializable, Cloneable {

    private static final long serialVersionUID = -5510967080302695103L;

    private Long shopId;
    private String shopName;
    @NotNull(message="选中状态不能为空")
    private Boolean check;
    private List<ProductCart> productList;
    @JSONField(serialize = false)
    private Integer productListSize;
    @JSONField(serialize = false)
    private BigDecimal shopMoney;

    public ShopCart(Long shopId, List<ProductCart> productList, Boolean check) {
        super();
        this.shopId = shopId;
        this.productList = productList;
        this.check = check;
    }

    public Integer getProductListSize() {
        return productList == null ? 0 : productList.size();
    }

}