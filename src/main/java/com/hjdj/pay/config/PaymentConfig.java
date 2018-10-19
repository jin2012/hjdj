package com.hjdj.pay.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Configuration
public class PaymentConfig implements WebMvcConfigurer, InitializingBean {
    @Autowired
    private ApplicationContext context;

    private String qrcodeHtmlTemplate;

    public String getQrcodeHtmlTemplate() {
        return qrcodeHtmlTemplate;
    }

    public void setQrcodeHtmlTemplate(String qrcodeHtmlTemplate) {
        this.qrcodeHtmlTemplate = qrcodeHtmlTemplate;
    }

    private static final String QRCODE_HTML_TEMPLATE_LOCATION = "classpath:static/qrcode.html";

    @Override
    public void afterPropertiesSet() {
        qrcodeHtmlTemplate = load(QRCODE_HTML_TEMPLATE_LOCATION);
    }

    private String load(String path){
        Resource resource = context.getResource(path);
        BufferedReader br = null;
        try {
            InputStream in = resource.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
