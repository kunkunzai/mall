package com.lk.mall.product.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lk.mall.product.dao.IGoodsDao;
import com.lk.mall.product.dao.IGoodsValueDao;
import com.lk.mall.product.dao.IProductDao;
import com.lk.mall.product.model.Goods;
import com.lk.mall.product.model.GoodsValue;
import com.lk.mall.product.model.Product;
import com.lk.mall.product.model.vo.GoodsVO;
import com.lk.mall.product.service.IGoodsService;

@Service
public class GoodsServiceImpl implements IGoodsService{
    
    @Autowired
    private IProductDao productDao;
    @Autowired
    private IGoodsDao goodsDao;
    @Autowired
    private IGoodsValueDao goodsValueDao;
    @Autowired
    private DozerBeanMapper mapper;

    @Override
    @Transactional
    public Integer saveGoods(Goods goods) {

        Optional<Product> p = productDao.findById(goods.getProductId());
        if (!p.isPresent()) {
            return 100;
        }
        Product product = p.get();
        if (product.getStatus() == 0) {
            product.setStatus(1);
        }
        Goods newGoods = goodsDao.save(goods);
        List<GoodsValue> goodsValueList = goods.getGoodsValueList();
        for (GoodsValue goodsValue : goodsValueList) {
            goodsValue.setGoodsId(newGoods.getId());
        }
        goodsValueDao.saveAll(goodsValueList);
        productDao.save(product);
        return 200;
    }

    @Override
    public List<GoodsVO> findGoodsDetail(List<Long> goodsIds) {
        List<Goods> goodsList = goodsDao.findAll((root, query, criteriaBuilder) -> {
            CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
            goodsIds.stream().forEach(x -> in.value(x));
            query.where(criteriaBuilder.and(in));
            return query.getRestriction();
        });
        if (goodsList.size() == 0) {
            return null;
        }
        List<Long> productIdList = goodsList.stream().map(Goods::getProductId).collect(Collectors.toList());
        List<Product> productList = productDao.findAllById(productIdList);
        if (productList.size() == 0) {
            return null;
        }
        List<GoodsVO> goodsVOList = new ArrayList<>();
        for (Goods goods : goodsList) {
            GoodsVO goodsVO = mapper.map(goods, GoodsVO.class);
            for (Product product : productList) {
                if (product.getId() == goods.getProductId()) {
                    goodsVO.setName(product.getName());
                    goodsVO.setDescription(product.getDescription());
                    goodsVO.setCode(product.getCode());
                    goodsVO.setShopId(product.getShopId());
                    goodsVO.setStatus(product.getStatus());
                }
            }
            goodsVOList.add(goodsVO);
        }
        return goodsVOList;
    }

}
