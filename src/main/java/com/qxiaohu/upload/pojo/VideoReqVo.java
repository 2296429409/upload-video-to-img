package com.qxiaohu.upload.pojo;

import lombok.Data;

/**
 * @author xiaxh
 * @date 2024/6/17
 */
@Data
public class VideoReqVo {

    /**
     * 导入文件
     */
    private String importFile;
    /**
     * 导出文件
     */
    private String exportFile;
    /**
     * 标签
     */
    private String cookie;
    /**
     * 标签
     */
    private String boardId;
    /**
     * 线程数
     */
    private Integer executorSize;


}
