package com.lk.mall.orders.Exception;

import java.math.BigDecimal;

public class IllegalPriceException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	private BigDecimal expectantPrice;
    
    private BigDecimal actualPrice;

	public IllegalPriceException(BigDecimal expectantPrice, BigDecimal actualPrice) {
		super();
		this.expectantPrice = expectantPrice;
		this.actualPrice = actualPrice;
	}

	public BigDecimal getExpectantPrice() {
		return expectantPrice;
	}

	public void setExpectantPrice(BigDecimal expectantPrice) {
		this.expectantPrice = expectantPrice;
	}

	public BigDecimal getActualPrice() {
		return actualPrice;
	}

	public void setActualPrice(BigDecimal actualPrice) {
		this.actualPrice = actualPrice;
	}
    


}
