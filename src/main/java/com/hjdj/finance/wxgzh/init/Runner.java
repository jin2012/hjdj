package com.hjdj.finance.wxgzh.init;

import com.hjdj.finance.wxgzh.beans.Wxgzh;
import com.hjdj.finance.wxgzh.service.WxgzhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 初始化运行
 *
 * @author jinshan
 */
@Component
public class Runner implements CommandLineRunner {


    @Autowired
    private WxgzhService wxgzhService;

    @Override
    public void run(String... args) throws Exception {
        List<Wxgzh> wxgzhs = wxgzhService.queryWxgzh();
        wxgzhService.addWxgzhRedis(wxgzhs);
    }
}
