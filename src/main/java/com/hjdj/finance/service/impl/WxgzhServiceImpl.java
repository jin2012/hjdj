package com.hjdj.finance.service.impl;

import com.hjdj.finance.beans.Wxgzh;
import com.hjdj.finance.repository.WxgzhRepository;
import com.hjdj.finance.service.WxgzhService;
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
    public void addWxgzhRedis(Integer id, Wxgzh wxgzh) {
        // redisTemplate.opsForHash().put("wxgzhs", id, wxgzh);
        redisTemplate.opsForValue().set(id, wxgzh);
    }

    @Override
    public Object queryWxgzhRedis(int i) {
        return redisTemplate.opsForValue().get(i);
    }

}
