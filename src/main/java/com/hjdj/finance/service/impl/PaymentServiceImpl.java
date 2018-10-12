package com.hjdj.finance.service.impl;

import com.hjdj.finance.beans.Order;
import com.hjdj.finance.service.PaymentService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    public RedisTemplate<String, Order> redisTemplate;

    @Override
    public void saveOrderRedis(Order order) {
        redisTemplate.opsForValue().set(order.getP2_Order(), order);
    }

    @Override
    public Order query(String p2_Order) {
        return redisTemplate.opsForValue().get(p2_Order);
    }

    @Override
    public boolean isRedisExist(String p2_order) {
        return redisTemplate.hasKey(p2_order);
    }

}
