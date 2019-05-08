package com.lk.mall.product.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "goods_value")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class GoodsValue {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long goodsId;
    @NotNull(message="货品属性id不能为空")
    private Integer propId;
    @NotEmpty(message="货品属性内容不能为空")
    private String propName;
    @NotNull(message="货品选项id不能为空")
    private Integer optionId;
    @NotEmpty(message="货品选项内容不能为空")
    private String optionValue;

}
