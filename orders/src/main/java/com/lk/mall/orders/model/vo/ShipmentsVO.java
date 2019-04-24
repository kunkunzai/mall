package com.lk.mall.orders.model.vo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShipmentsVO {

    @NotNull(message="商家ID不能为空")
    private Long shopId;
    @NotEmpty(message="订单号不能为空")
    private String orderId;
    @NotEmpty(message="物流公司不能为空")
    private String logisticsCompany;
    @NotEmpty(message="物流公司编号不能为空")
    private String logisticsCompanyCode;
    @NotEmpty(message="物流单号号不能为空")
    private String logisticsNo;
    
}
