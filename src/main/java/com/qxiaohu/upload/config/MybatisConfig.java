package com.qxiaohu.upload.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaxh
 * @date 2024/11/8
 */
@Configuration
public class MybatisConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        // 初始化 MybatisPlusInterceptor 核心插件
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加自动分页插件 PaginationInnerInterceptor
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // 返回
        return interceptor;
    }

}
