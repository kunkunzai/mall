package com.lk.mall.product.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.product.model.Product;
import com.lk.mall.product.model.vo.ProductVO;
import com.lk.mall.product.service.IProductService;

@RestController
public class ProductController {

	@Autowired
	private IProductService productService;

	@RequestMapping("/findByProductId")
	public ProductVO findByProductId(@RequestParam("productId") Long productId) {
		return productService.findById(productId);
	}

	@RequestMapping("/findAllByProductId")
	public List<Product> findAllById(@RequestParam("productIds") String productIds) {
		if (productIds.isEmpty()) {
			return null;
		}
		List<Long> newList = new ArrayList<>();
		String[] ids = productIds.split(",");
		for (String id : ids) {
			newList.add(Long.parseLong(id));
		}
		return productService.findAllById(newList);
	}

	@RequestMapping("/findProductByShopId")
	public Page<Product> findproductByShopId(
			@RequestParam(value = "shopId") Long shopId,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "2") Integer size) {
		return productService.findByShopId(shopId, 1, page, size);
	}

	@RequestMapping("/saveProduct")
	public Product saveproduct(@RequestBody Product product) {
		System.err.println(product.toString());
		return productService.save(product);
	}

}
