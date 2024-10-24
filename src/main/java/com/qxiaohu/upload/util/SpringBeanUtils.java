package com.qxiaohu.upload.util;

import cn.hutool.extra.spring.SpringUtil;
import com.qxiaohu.upload.config.ContextBeanConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Map;

/**
 * @author xiaxh
 * @date 2023/12/14
 */
public class SpringBeanUtils {

    /**
     * 根据标识 获取容器对象
     *
     * @param code
     * @return
     */
    public static <T> T getBean(String code, Class<T> tClass) {
        Map<String, T> beansOfType = SpringUtil.getBeansOfType(tClass);
        for (Map.Entry<String, T> entry : beansOfType.entrySet()) {
            if (entry.getKey().equals(code)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 根据构造参数注入
     *
     * @param beanName
     * @param clazz
     * @param args
     * @param <T>
     * @return
     */
    public static <T> T registerBeanConstruct(String beanName, Class<T> clazz, Object... args) {
        ContextBeanConfig beanConfig = SpringUtil.getBean(ContextBeanConfig.class);
        // 创建bean信息.
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        if (args.length > 0) {
            for (Object arg : args) {
                beanDefinitionBuilder.addConstructorArgValue(arg);
            }
        }
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) beanConfig.getApplicationContext();
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        return beanConfig.getApplicationContext().getBean(beanName, clazz);
    }


    /**
     * 删除 beandefinition
     *
     * @param beanName
     */
    public static void removeBeanDefinition(String beanName) {
        ContextBeanConfig beanConfig = SpringUtil.getBean(ContextBeanConfig.class);
        // 获取BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanConfig.getApplicationContext()
                .getAutowireCapableBeanFactory();
        defaultListableBeanFactory.removeBeanDefinition(beanName);
    }
}
