package com.lk.mall.orders.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lk.mall.orders.model.response.ProductServiceResponse;

@FeignClient(value = "service-product")
public interface IProductService {
    @RequestMapping(value = "/findByProductId",method = RequestMethod.GET)
    ProductServiceResponse findByProductId(@RequestParam(value = "productId") Long productId);
    
    @RequestMapping(value = "/findAllByProductId",method = RequestMethod.GET)
    List<ProductServiceResponse> findAllByProductId(@RequestParam(value = "productIdList") List<Long> productIdList);
}