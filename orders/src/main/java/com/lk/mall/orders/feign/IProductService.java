package com.lk.mall.orders.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lk.mall.orders.model.response.ProductServiceResponse;

@FeignClient(value = "service-product")
public interface IProductService {
    
    @RequestMapping(value = "/findGoodsDetail",method = RequestMethod.GET)
    List<ProductServiceResponse> findGoodsDetail(@RequestParam(value = "goodsIds") List<Long> goodsIds);
}