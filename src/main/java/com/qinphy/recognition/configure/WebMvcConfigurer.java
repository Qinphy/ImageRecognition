package com.qinphy.recognition.configure;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author: Qinphy
 * @Description: Web安全配置
 * @date: 2020/6/24 10:08
 */
public class WebMvcConfigurer extends WebMvcConfigurationSupport {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 编程使用/uploads/**路径会解析到磁盘的/home/qinphy/uploads/
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:/home/qinphy/uploads/");
    }
}
