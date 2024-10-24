package com.qxiaohu.upload.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 获取spring上下文
 */
@Component
public class ContextBeanConfig {

    private final ConfigurableApplicationContext applicationContext;


    public ContextBeanConfig(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
