package com.qxiaohu.upload.config;

import lombok.Data;

/**
 * 文件上传配置   
 * @author xiaxh
 * @date 2024/7/11
 */
@Data
public class UploadFileConfig {

    public static final String KEY = "uploadFile_";

    private String name;
    private String url;
    private String authorization;


    public UploadFileConfig(String name, String url, String authorization) {
        this.name = name;
        this.url = url;
        this.authorization = authorization;
    }
}
