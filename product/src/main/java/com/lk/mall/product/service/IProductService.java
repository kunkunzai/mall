package com.lk.mall.product.service;

import java.util.List;

import com.lk.mall.product.model.Product;
import com.lk.mall.product.model.vo.CollectVO;

public interface IProductService {

    List<Product> findByShopId(Long shopId);
    
    Product findById(Long productId);
    
    Product saveProduct(Product product);
    
    Integer updateProductStatus(Long productId, Integer status);
    
    Integer collectProduct(Long productId, Long userId, Integer type);
    
    List<CollectVO> findCollectVOList(Long userId);
    
}
