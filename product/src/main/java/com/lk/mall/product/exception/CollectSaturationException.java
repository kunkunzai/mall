package com.lk.mall.product.exception;

public class CollectSaturationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer quantity;

    public CollectSaturationException(Integer quantity) {
        super();
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
}
