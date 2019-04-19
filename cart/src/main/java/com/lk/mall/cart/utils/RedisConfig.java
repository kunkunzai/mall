package com.lk.mall.cart.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@ComponentScan("com.uniquewings.springboot.redisDemo")
public class RedisConfig {

    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Bean
    public RedisTemplate<Object, Object> redisTemplate ()
    {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        //如果不设置，将使用默认的 Serializer
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new RedisObjectSerializer());;
        return template;
    }
}
