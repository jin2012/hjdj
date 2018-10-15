package com.hjdj.finance.wxgzh.service;

import com.hjdj.finance.wxgzh.beans.Order;

public interface PaymentService {

    void saveOrderRedis(Order order);

    Order query(String p2_Order);

    boolean isRedisExist(String p2_order);
}
