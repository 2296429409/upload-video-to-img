package com.qxiaohu.upload.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qxiaohu.upload.db.XiaohuVideo;
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
    List<XiaohuVideo> save(String filePath);

    Page<XiaohuVideo> page(String name, Long page, Long size);

    /**
     * 删除视频记录
     * @param id 视频ID
     * @return 是否删除成功
     */
    boolean removeById(Long id);

    /**
     * 新增视频记录
     * @param video 视频信息
     * @return 是否新增成功
     */
    boolean save(XiaohuVideo video);

    /**
     * 更新视频记录
     * @param video 视频信息
     * @return 是否更新成功
     */
    boolean updateById(XiaohuVideo video);
}
