package com.qxiaohu.upload.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author xiaxh
 * @date 2024/6/17
 */
@Data
public class VideoStatusRespVo {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 状态
     */
    private String status;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 任务参数
     */
    private VideoReqVo taskParams;

}
