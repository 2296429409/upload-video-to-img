package com.qxiaohu.upload.config;

import com.qxiaohu.upload.util.JavtrailersUtil;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaxh
 * @date 2024/11/8
 */
@Configuration
public class OkHttpConfig {

    @Resource
    private CommonConfig config;

    @Bean
    public OkHttpClient getInstance() {
        return new OkHttpClient.Builder()
                .proxy(
                        new Proxy(Proxy.Type.HTTP,
                                new InetSocketAddress(config.getProxyIp(), config.getProxyPort())))
                .connectTimeout(1000L, TimeUnit.SECONDS)
                .readTimeout(1000L, TimeUnit.SECONDS)
                .writeTimeout(1000L, TimeUnit.SECONDS)
                .build();
    }
}
