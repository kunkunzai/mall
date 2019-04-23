package com.lk.mall.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotEmpty;
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

	@NotNull(message="店铺id不能为空")
	private Long shopId;
	private String shopName;
	private Boolean check;
	@NotEmpty(message="商品列表不能为空")
	private List<ProductCart> productList;
	@JSONField(serialize = false)
	private Boolean isAll;
	@JSONField(serialize = false)
	private Integer productListSize;
	@JSONField(serialize = false)
	private BigDecimal shopMoney;
	
	
    public ShopCart(Long shopId, List<ProductCart> productList, Boolean isAll, Boolean check) {
        super();
        this.shopId = shopId;
        this.productList = productList;
        this.isAll = isAll;
        this.check = check;
    }
    
    public Integer getProductListSize() {
        return productList.size();
    }
    
    public BigDecimal getShopMoney() {
        BigDecimal shopMoney = BigDecimal.ZERO;
        for (ProductCart productCart : productList) {
            shopMoney = shopMoney.add(productCart.getProductMoney().multiply(new BigDecimal(productCart.getQuantity().toString())));
        }
        return shopMoney;
    }
    
	
}
