package com.lk.mall.product.handler;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.IOUtils;
import com.lk.mall.product.exception.CollectSaturationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String msg = null;
        if (ex instanceof CollectSaturationException) {
            msg = "收藏列表里的商品数为" + ((CollectSaturationException) ex).getQuantity().toString() + ",您已超出限制";
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
