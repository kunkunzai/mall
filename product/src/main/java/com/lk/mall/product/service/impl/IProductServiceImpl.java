package com.lk.mall.product.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.lk.mall.product.dao.IProductDao;
import com.lk.mall.product.model.Product;
import com.lk.mall.product.model.vo.ProductVO;
import com.lk.mall.product.service.IProductService;

@Service
public class IProductServiceImpl implements IProductService {

	@Autowired
	private DozerBeanMapper mapper;

	@Autowired
	private IProductDao productDao;

	@Override
	public Product save(Product product) {
		product.setStatus(0);
		product.setCreateTime(LocalDateTime.now());
		return productDao.save(product);
	}

	@Override
	public ProductVO findById(Long id) {
		Optional<Product> product = productDao.findById(id);
		if (product.isPresent()) {
			return mapper.map(product.get(), ProductVO.class);
		}
		return new ProductVO();
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
		return productList;
	}

}
