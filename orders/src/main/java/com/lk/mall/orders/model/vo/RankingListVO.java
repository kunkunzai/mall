package com.lk.mall.orders.model.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class RankingListVO{
    
    private List<Ranking> rankingList;
    
    @Data
    @ToString
    public static class Ranking{
        private Long productId;
        private Double quantity;
        private String productName;
        
    }
	
}
