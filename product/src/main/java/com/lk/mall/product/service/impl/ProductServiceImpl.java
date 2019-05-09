package com.lk.mall.product.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;

import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lk.mall.product.constant.RedisConstant;
import com.lk.mall.product.dao.IGoodsDao;
import com.lk.mall.product.dao.IGoodsValueDao;
import com.lk.mall.product.dao.IProductDao;
import com.lk.mall.product.dao.IProductPropDao;
import com.lk.mall.product.model.Collect;
import com.lk.mall.product.model.Goods;
import com.lk.mall.product.model.GoodsValue;
import com.lk.mall.product.model.Product;
import com.lk.mall.product.model.ProductProp;
import com.lk.mall.product.model.vo.CollectVO;
import com.lk.mall.product.service.IProductService;

@Service
public class ProductServiceImpl implements IProductService{
    
    @Autowired
    private IProductDao productDao;
    @Autowired
    private IGoodsDao goodsDao;
    @Autowired
    private IGoodsValueDao goodsValueDao;
    @Autowired
    private IProductPropDao productPropDao;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<Product> findByShopId(Long shopId) {
        List<Product> productList = productDao.findByShopId(shopId);
        return productList;
    }

    @Override
    public Product findById(Long productId) {
        Optional<Product> p = productDao.findById(productId);
        if (!p.isPresent()) {
            return null;
        }
        List<Goods> goodsList = goodsDao.findByProductId(productId);
        List<Long> goodsIdList = goodsList.stream().map(Goods::getId).collect(Collectors.toList());
        List<GoodsValue> goodsValueList = goodsValueDao.findAll((root, query, criteriaBuilder) -> {
            CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("goodsId"));
            goodsIdList.stream().forEach(x -> in.value(x));
            query.where(criteriaBuilder.and(in));
            return query.getRestriction();
        });
        for (Goods goods : goodsList) {
            List<GoodsValue> newGoodsValueList = new ArrayList<>();
            for (GoodsValue goodsValue : goodsValueList) {
                if (goods.getId() == goodsValue.getGoodsId()) {
                    newGoodsValueList.add(goodsValue);
                }
            }
            goods.setGoodsValueList(newGoodsValueList);
        }
        Product product = p.get();
        product.setGoodsList(goodsList);
        return product;
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        product.setCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8));
        product.setStatus(0);
        Product newProduct = productDao.saveAndFlush(product);
        List<ProductProp> productProList = new ArrayList<>(product.getPropList().size());
        for (Long propId : product.getPropList()) {
            ProductProp productPro = new ProductProp();
            productPro.setProductId(newProduct.getId());
            productPro.setPropId(propId);
            productProList.add(productPro);
        }
        productPropDao.saveAll(productProList);
        return newProduct;
    }

    @Override
    public Integer updateProductStatus(Long productId, Integer status) {
        Optional<Product> p = productDao.findById(productId);
        if(!p.isPresent()) {
            return 100;
        }
        Product product = p.get();
        product.setStatus(status);
        productDao.saveAndFlush(product);
        return 200;
    }
    
    @Override
    public Integer collectProduct(Long productId, Long userId, Integer type) {
        String key = RedisConstant.COLLECT_PRODUCT_PREFIX + userId;
        Collect latestCollect = new Collect(userId, productId);
        RList<Collect> list = redissonClient.getList(key);
        if (list.isExists()) {
            if (type == 1) {
//            存在
//                收藏操作
                if (!list.contains(latestCollect)) {
                    list.add(latestCollect);
                }
            } else {
//                取消收藏
                if (list.contains(latestCollect)) {
                    list.remove(latestCollect);
                }
            }
        } else {
//            不存在
            if (type == 1) {
                list.add(latestCollect);
            }
        }
        return 200;
    }

    @Override
    public List<CollectVO> findCollectVOList(Long userId) {
        String key = RedisConstant.COLLECT_PRODUCT_PREFIX + userId;
        RList<Collect> list = redissonClient.getList(key);
        if (list.isExists()) {
            List<Long> productIdList = list.stream().map(Collect::getProductId).collect(Collectors.toList());
            List<Product> productList = productDao.findAll((root, query, criteriaBuilder) -> {
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
                productIdList.stream().forEach(x -> in.value(x));
                query.where(criteriaBuilder.and(in));
                return query.getRestriction();
            });
            List<CollectVO> collectVOList = new ArrayList<>(list.size());
            for (Collect collect : list) {
                for (Product product : productList) {
                    if (collect.getProductId() == product.getId()) {
                        CollectVO collectVO = new CollectVO();
                        collectVO.setProductId(product.getId());
                        collectVO.setName(product.getName());
                        collectVO.setPrice(product.getPrice());
                        collectVOList.add(collectVO);
                    }
                }
            }
            return collectVOList;
        }
        return null;
    }

}
