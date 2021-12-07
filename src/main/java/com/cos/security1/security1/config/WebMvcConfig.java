package com.cos.security1.security1.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /*
          머스테치 사용함 , 머스테치는 템플릿 엔진임
          머스테치 기본폴더는 src/main/resources/ 로 잡힘
          자동으로 잡히는 뷰리졸버 설정 : templates (prefix) , .mustache(suffix)
     */

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) { //머스테치 사용으로 기본적으로 잡힌 설정을 내가 재설정한것
        MustacheViewResolver resolver = new MustacheViewResolver();
        resolver.setCharset("UTF-8");
        resolver.setContentType("text/html; charset=UTF-8");
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");

        registry.viewResolver(resolver); //뷰 리졸버 등록
    }
}
