package com.lk.mall.product.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.lk.mall.product.constant.RedisConstant;
import com.lk.mall.product.dao.IGoodsDao;
import com.lk.mall.product.dao.IGoodsValueDao;
import com.lk.mall.product.dao.IProductDao;
import com.lk.mall.product.dao.IShopDao;
import com.lk.mall.product.exception.CollectSaturationException;
import com.lk.mall.product.model.Goods;
import com.lk.mall.product.model.GoodsValue;
import com.lk.mall.product.model.Product;
import com.lk.mall.product.model.Shop;
import com.lk.mall.product.model.vo.CollectVO;
import com.lk.mall.product.model.vo.GoodsVO;
import com.lk.mall.product.service.IGoodsService;
import com.lk.mall.product.util.RedisUtil;

@Service
@PropertySource({"classpath:system.properties"})
public class GoodsServiceImpl implements IGoodsService{
    
    @Autowired
    private IShopDao shopDao;
    @Autowired
    private IProductDao productDao;
    @Autowired
    private IGoodsDao goodsDao;
    @Autowired
    private IGoodsValueDao goodsValueDao;
    @Autowired
    private DozerBeanMapper mapper;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${collect.product.quantity.limit}")
    private Integer quantityLimit;

    @Override
    @Transactional
    public Integer saveGoods(Goods goods) {

        Optional<Product> p = productDao.findById(goods.getProductId());
        if (!p.isPresent()) {
            return 100;
        }
        Product product = p.get();

        Goods newGoods = goodsDao.save(goods);
        List<GoodsValue> goodsValueList = goods.getGoodsValueList();
        for (GoodsValue goodsValue : goodsValueList) {
            goodsValue.setGoodsId(newGoods.getId());
        }
        goodsValueDao.saveAll(goodsValueList);
        if (null == product.getPrice() || goods.getPrice().compareTo(product.getPrice()) == -1) {
            product.setPrice(goods.getPrice());
            productDao.saveAndFlush(product);
        }
        return 200;
    }

    @Override
    public List<GoodsVO> findGoodsDetail(List<Long> goodsIds) {
//        得到所有goods列表
        List<Goods> goodsList = goodsDao.findAll((root, query, criteriaBuilder) -> {
            CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
            goodsIds.stream().forEach(x -> in.value(x));
            query.where(criteriaBuilder.and(in));
            return query.getRestriction();
        });
        if (goodsList.size() == 0) {
            return null;
        }
//        取出所有productId
        List<Long> productIdList = goodsList.stream().map(Goods::getProductId).collect(Collectors.toList());
        List<Product> productList = productDao.findAllById(productIdList);
        if (productList.size() == 0) {
            return null;
        }
//        组装数据
        List<Long> shopIdList = new ArrayList<>();
        List<GoodsVO> goodsVOList = new ArrayList<>();
        for (Goods goods : goodsList) {
            GoodsVO goodsVO = mapper.map(goods, GoodsVO.class);
            for (Product product : productList) {
                if (product.getId() == goods.getProductId()) {
                    goodsVO.setName(product.getName());
                    goodsVO.setDescription(product.getDescription());
                    goodsVO.setCode(product.getCode());
                    goodsVO.setShopId(product.getShopId());
                    shopIdList.add(product.getShopId());
                    goodsVO.setStatus(product.getStatus());
                    goodsVO.setImage(product.getImage());
                }
            }
            goodsVOList.add(goodsVO);
        }
        List<Shop> shopList = shopDao.findAll((root, query, criteriaBuilder) -> {
            CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
            shopIdList.stream().forEach(x -> in.value(x));
            query.where(criteriaBuilder.and(in));
            return query.getRestriction();
        });
        for (GoodsVO goodVO : goodsVOList) {
            for (Shop shop : shopList) {
                if (goodVO.getShopId() == shop.getId()) {
                    goodVO.setShopName(shop.getName());
                }
            }
        }
        return goodsVOList;
    }

	@Override
	public Integer collectGoodsQuantity(Long userId) {
		String key = RedisConstant.COLLECT_PRODUCT_PREFIX + userId;
		List<Object> list = redisUtil.lGet(key, 0, -1);
		return list.size();
	}
    
    @Override
    public Integer collectGoods(Long goodsId, Long userId, Integer type) {
        String key = RedisConstant.COLLECT_PRODUCT_PREFIX + userId;
        String userList = RedisConstant.PRODUCT_SET_PREFIX + goodsId;
        List<Object> list = redisUtil.lGet(key, 0, -1);
        if (!list.isEmpty()) {
            List<Long> newList = new ArrayList<>(list.size());
            for (Object object : list) {
                newList.add(Long.parseLong(object.toString()));
            }
            if (type == 1) {
//            存在
//                收藏操作
                if (!newList.contains(goodsId)) {
                    if (newList.size() >= quantityLimit) {
                        throw new CollectSaturationException(quantityLimit);
                    }
                    redisUtil.lSet(key, goodsId);
                    redisUtil.sSet(userList, userId);
                }
            } else {
//                取消收藏
                if (newList.contains(goodsId)) {
                    redisUtil.lRemove(key, 1, goodsId);
                    redisUtil.setRemove(userList, userId);
                }
            }
        } else {
//            不存在
            if (type == 1) {
                redisUtil.lSet(key, goodsId);
                redisUtil.sSet(userList, userId);
            }
        }
        return 200;
    }

    @Override
    public List<CollectVO> findCollectVOList(Long userId) {
        String key = RedisConstant.COLLECT_PRODUCT_PREFIX + userId;
        List<Object> list = redisUtil.lGet(key, 0, -1);
        List<Long> newList = new ArrayList<>(list.size());
        for (Object object : list) {
            newList.add(Long.parseLong(object.toString()));
        }
        if (!list.isEmpty()) {
            List<GoodsVO> goodsList = findGoodsDetail(newList);
            List<CollectVO> collectVOList = new ArrayList<>(list.size());
            for (Long id : newList) {
                for (GoodsVO goods : goodsList) {
                    if (id == goods.getId()) {
                        CollectVO collectVO = new CollectVO();
                        collectVO.setProductId(goods.getProductId());
                        collectVO.setGoodsId(goods.getId());
                        collectVO.setName(goods.getName());
                        collectVO.setPrice(goods.getPrice());
                        collectVO.setImage(goods.getImage());
                        collectVOList.add(collectVO);
                    }
                }
            }
            return collectVOList;
        }
        return null;
    }

	@Override
	public Long findCollectUserQuantity(Long goodsId) {
		String key = RedisConstant.PRODUCT_SET_PREFIX + goodsId;
		long size = redisUtil.sGetSetSize(key);
		return size;
	}
}
