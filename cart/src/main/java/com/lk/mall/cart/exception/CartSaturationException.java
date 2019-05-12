package com.lk.mall.cart.exception;

public class CartSaturationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer quantity;

    public CartSaturationException(Integer quantity) {
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
