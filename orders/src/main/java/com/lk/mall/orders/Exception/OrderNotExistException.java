package com.lk.mall.orders.Exception;

public class OrderNotExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String orderId;

    public OrderNotExistException(String orderId) {
        super();
        this.orderId = orderId;
    }

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
    
}
