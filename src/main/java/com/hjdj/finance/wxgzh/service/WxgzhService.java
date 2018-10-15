package com.hjdj.finance.wxgzh.service;

import com.hjdj.finance.wxgzh.beans.Wxgzh;

import java.util.List;

public interface WxgzhService {
    List<Wxgzh> queryWxgzh();

    void addWxgzhRedis(List<Wxgzh> wxgzh);

    List<Wxgzh> queryWxgzhsRedis();
}
