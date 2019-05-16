package com.lk.mall.product.service;

import java.util.List;

import com.lk.mall.product.model.Goods;
import com.lk.mall.product.model.vo.CollectVO;
import com.lk.mall.product.model.vo.GoodsVO;

public interface IGoodsService {

    Integer saveGoods(Goods goods);
    
    List<GoodsVO> findGoodsDetail(List<Long> goodsIds);
    
    Integer collectGoodsQuantity(Long userId);
    
    Integer collectGoods(Long goodsId, Long userId, Integer type);
    
    List<CollectVO> findCollectVOList(Long userId);
    
    Long findCollectUserQuantity(Long goodsId);

}
