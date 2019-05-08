package com.lk.mall.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.product.model.Product;
import com.lk.mall.product.service.IProductService;


@RestController
public class ProductController {
    
    @Autowired
    private IProductService productService;
    
    
    @RequestMapping("/findProductByShopId")
    public List<Product> findProductByShopId(@RequestParam("shopId") Long shopId) {
        return productService.findByShopId(shopId);
    }
    
    @RequestMapping("/findProductById")
    public Product findProductById(@RequestParam("productId") Long productId) {
        return productService.findById(productId);
    }
    
    @RequestMapping("/saveProduct")
    public Product saveProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

}
