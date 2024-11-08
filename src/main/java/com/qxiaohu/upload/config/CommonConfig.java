package com.qxiaohu.upload.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaxh
 * @date 2024/11/8
 */
@Configuration
@Data
public class CommonConfig {

    @Value("${http.proxy.ip}")
    private String proxyIp;

    @Value("${http.proxy.port}")
    private Integer proxyPort;


}
