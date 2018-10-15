package com.hjdj.finance.wxgzh.service.impl;

import com.hjdj.finance.wxgzh.beans.Wxgzh;
import com.hjdj.finance.wxgzh.repository.WxgzhRepository;
import com.hjdj.finance.wxgzh.service.WxgzhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WxgzhServiceImpl implements WxgzhService {

    @Autowired
    private WxgzhRepository wxgzhRepository;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public List<Wxgzh> queryWxgzh() {
        return wxgzhRepository.findAll();
    }

    @Override
    public void addWxgzhRedis(List<Wxgzh> wxgzh) {
        redisTemplate.opsForValue().set("wxgzhs", wxgzh);
    }

    @Override
    public List<Wxgzh> queryWxgzhsRedis() {
        return (List<Wxgzh>)redisTemplate.opsForValue().get("wxgzhs");
    }

}
