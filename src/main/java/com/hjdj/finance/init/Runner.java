package com.hjdj.finance.init;

import com.hjdj.finance.beans.Wxgzh;
import com.hjdj.finance.service.WxgzhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ��ʼ������
 *
 * @author jinshan
 */
@Component
public class Runner implements CommandLineRunner {
    // ��ѭ����
    public static volatile int val = 1;

    // ��ѭ�����
    public static volatile int indexLx = 1;

    @Autowired
    private WxgzhService wxgzhService;

    @Override
    public void run(String... args) throws Exception {
        List<Wxgzh> wxgzhs = wxgzhService.queryWxgzh();
        for(int i = 0; i < wxgzhs.size(); i ++){
            wxgzhService.addWxgzhRedis(wxgzhs.get(i).getId(), wxgzhs.get(i));
        }

    }
}
