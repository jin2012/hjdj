package com.hjdj.finance.service;

import com.hjdj.finance.beans.Wxgzh;

import java.util.List;

public interface WxgzhService {
    List<Wxgzh> queryWxgzh();

    void addWxgzhRedis(Integer id, Wxgzh wxgzh);

    Object queryWxgzhRedis(int i);
}
