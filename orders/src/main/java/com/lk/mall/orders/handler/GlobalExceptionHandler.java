package com.lk.mall.orders.handler;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.IOUtils;
import com.lk.mall.orders.Exception.IllegalPriceException;
import com.lk.mall.orders.Exception.OrderNotExistException;
import com.lk.mall.orders.Exception.OrderStatusException;
import com.lk.mall.orders.Exception.ServicesNotConnectedException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = RuntimeException.class)
	public ModelAndView defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		String msg = null;
		if (ex instanceof OrderNotExistException) {
			msg = "订单号：" + ((OrderNotExistException) ex).getOrderId() + "不存在!";
		} else if (ex instanceof IllegalPriceException) {
			msg = "错误价格：" + ((IllegalPriceException) ex).getActualPrice().toString();
		} else if (ex instanceof OrderStatusException) {
			msg = "订单号：" + ((OrderStatusException) ex).getOrderId() + ",不可支付!";
		} else if (ex instanceof ServicesNotConnectedException) {
			msg = ex.getMessage();
		} else {
			msg = "未知异常!";
		}
		ex.printStackTrace();
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(JSON.toJSON(msg.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(out);
		}
		return null;
	};

}
