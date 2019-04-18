package com.lk.mall.orders.Exception;

public class OrderStatusException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

    private String orderId;

    public OrderStatusException(String orderId) {
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
