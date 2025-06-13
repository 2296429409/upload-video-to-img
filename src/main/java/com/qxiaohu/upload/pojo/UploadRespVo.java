package com.qxiaohu.upload.pojo;

import lombok.Data;

import java.io.File;

/**
 * @author xiaxh
 * @date 2024/10/24
 */
@Data
public class UploadRespVo {
    private Integer id;
    private String key;
    private boolean result;
    private File errFile;
    private int errCount;
}
