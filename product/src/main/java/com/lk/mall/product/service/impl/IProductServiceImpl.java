package com.lk.mall.product.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.lk.mall.product.dao.IProductDao;
import com.lk.mall.product.dao.IShopDao;
import com.lk.mall.product.model.Product;
import com.lk.mall.product.model.Shop;
import com.lk.mall.product.service.IProductService;

@Service
public class IProductServiceImpl implements IProductService {

	@Autowired
	private IProductDao productDao;
	
	@Autowired
	private IShopDao shopDao;

	@Override
	public Product save(Product product) {
		product.setStatus(0);
		product.setCreateTime(LocalDateTime.now());
		return productDao.save(product);
	}

	@Override
	public Product findById(Long id) {
		Optional<Product> product = productDao.findById(id);
		if (product.isPresent()) {
			return product.get();
		}
		return new Product();
	}

	@Override
	public Page<Product> findByShopId(final Long shopId, final Integer status, Integer page, Integer size) {
		PageRequest pageable = PageRequest.of(page, size, Sort.Direction.DESC, "updateTime");
		Page<Product> productList = productDao.findAll((root, query, criteriaBuilder) -> {
			Predicate p1 = criteriaBuilder.equal(root.get("shopId").as(Long.class), shopId);
			Predicate p2 = criteriaBuilder.equal(root.get("status").as(Integer.class), status);
			query.where(criteriaBuilder.and(p1, p2));
			return query.getRestriction();
		}, pageable);
		return productList;
	}

	@Override
	public List<Product> findAllById(List<Long> ids) {
		List<Product> productList = productDao.findAllById(ids);
		if (productList.isEmpty()) {
			return null;
		}
		List<Long> sids = productList.stream().map(Product::getShopId).collect(Collectors.toList());
		List<Shop> shopList = shopDao.findAllById(sids);
		productList.forEach(x -> {
			shopList.forEach(y -> {
				if (x.getShopId() == y.getId()) {
					x.setShopName(y.getName());
				}
			});
		});
		return productList;
	}

}
