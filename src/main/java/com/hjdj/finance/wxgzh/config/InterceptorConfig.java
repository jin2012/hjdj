package com.hjdj.finance.wxgzh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ����������Ȩ��
 *
 * @author jinshan
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
/*
    @Autowired
    public RequestInterceptor requestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor).excludePathPatterns("/pay")
                                                    .excludePathPatterns("/code")
                                                    .excludePathPatterns("/payorder")
                                                    .excludePathPatterns("/error.html")
                                                    .excludePathPatterns("/image/error.jpg");
    }*/

}
