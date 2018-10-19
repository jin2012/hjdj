package com.hjdj.pay.service;

import com.hjdj.pay.beans.Order;

public interface PaymentService {

    void saveOrderRedis(Order order);

    Order query(String p2_Order);

    boolean isRedisExist(String p2_order);

    void deleteOrderRedis(String order);
}
