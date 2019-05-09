package com.lk.mall.product.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
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

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Product {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JSONField(serialize = false)
    private Long id;
    @NotNull(message="目录id不能为空")
    private Long directoryId;
    @NotEmpty(message="商品名称不能为空")
    private String name;
    @NotEmpty(message="商品描述不能为空")
    private String description;
    private String code;
    @NotEmpty(message="商品图片不能为空")
    private String image;
    @NotNull(message="商家id不能为空")
    private Long shopId;
    @NotNull(message="商品价格不能为空")
    @DecimalMin(value = "0.01")
    private BigDecimal price;
    private Integer status;
    @Transient
    @Valid
    private List<Goods> goodsList;
    @Transient
    private List<Long> propList;

    public List<Goods> getGoodsList() {
        if (null == goodsList) {
            return new ArrayList<>();
        }
        return goodsList;
    }

}
