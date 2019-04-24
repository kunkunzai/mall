package com.lk.mall.orders.model.vo;

import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class CornerMarkVO {

    private Long countWaitPay;
    private Long countWaitSend;
    private Long countWaitReceive;
    private Long countWaitEvaluate;
    
    
    public Long getCountWaitPay() {
        return countWaitPay == null ? 0 : countWaitPay;
    }

    public Long getCountWaitSend() {
        return countWaitSend == null ? 0 : countWaitSend;
    }

    public Long getCountWaitReceive() {
        return countWaitReceive == null ? 0 : countWaitReceive;
    }

    public Long getCountWaitEvaluate() {
        return countWaitEvaluate == null ? 0 : countWaitEvaluate;
    }
    
}
