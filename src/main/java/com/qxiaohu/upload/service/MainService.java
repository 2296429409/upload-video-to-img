package com.qxiaohu.upload.service;

import com.qxiaohu.upload.pojo.VideoReqVo;
import com.qxiaohu.upload.pojo.VideoStatusRespVo;

import java.util.List;

/**
 * @author xiaxh
 * @date 2024/6/17
 */
public interface MainService {
    /**
     * 创建处理word文件任务
     * @param reqVo
     * @return
     */
    List<String> startWordTask(VideoReqVo reqVo);

    /**
     * 任务执行状态
     * @return
     */
    List<VideoStatusRespVo> status();

    /**
     * 获取视频
     * @return
     */
    void getVideoInfo(String filePath);

}
