package com.hjdj.finance.init;

import com.hjdj.finance.beans.Wxgzh;
import com.hjdj.finance.service.WxgzhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.management.Query;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

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
