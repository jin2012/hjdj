package com.hjdj.finance.service;

import com.hjdj.finance.beans.Wxgzh;

import java.awt.geom.QuadCurve2D;
import java.util.List;
import java.util.Queue;

public interface WxgzhService {
    List<Wxgzh> queryWxgzh();

    void addWxgzhRedis(List<Wxgzh> wxgzh);

    List<Wxgzh> queryWxgzhsRedis();
}
