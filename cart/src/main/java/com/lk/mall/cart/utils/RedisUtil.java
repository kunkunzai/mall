package com.lk.mall.cart.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisUtil {

    @Autowired
    private StringRedisTemplate template;


    /**
     * set方法
     * @param key
     * @param value
     */
    public void set(String key, String value){
        ValueOperations<String, String> ops = template.opsForValue();
        ops.set(key,value);
    }

    /**
     * get方法
     * @param key
     * @return
     */
    public String get(String key){
        ValueOperations<String, String> ops = template.opsForValue();
        return ops.get(key);
    }

}
