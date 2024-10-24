package com.qxiaohu.upload.word;

import com.qxiaohu.upload.pojo.UploadRespVo;
import com.qxiaohu.upload.pojo.VideoReqVo;
import com.qxiaohu.upload.pojo.VideoStatusRespVo;
import com.qxiaohu.upload.util.FfmpegUtil;
import com.qxiaohu.upload.util.UploadFileUtil;
import com.qxiaohu.upload.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaxh
 * @date 2024/6/18
 */
@Slf4j
@Component
public class FfmpegAsync {

    private final static ConcurrentHashMap<String, VideoStatusRespVo> cache = new ConcurrentHashMap<>();

    /**
     * 异步执行
     *
     * @param reqVo
     * @param cache
     */
    @Async
    public void execVideoTask(VideoReqVo reqVo, Map<String, VideoStatusRespVo> cache) {
        Set<String> fileNames = new HashSet<>(cache.keySet());
        fileNames.forEach(fileName -> {
            VideoStatusRespVo videoStatusRespVo = cache.get(fileName);
            videoStatusRespVo.setStartTime(new Date());
            videoStatusRespVo.setStatus("执行中");
            String m3u8File = FfmpegUtil.run(reqVo.getImportFile() + File.separator + fileName);
            String changeImg = ImageUtil.fileChangeImg(m3u8File, reqVo.getExportFile());
            Map<String, UploadRespVo> uploadRespVoMap = UploadFileUtil.uploadPhotosExecutor(changeImg, reqVo);
            UploadFileUtil.backups(uploadRespVoMap, reqVo);
            ImageUtil.setPlayAddress(m3u8File, uploadRespVoMap, reqVo.getExportFile());
            videoStatusRespVo.setStatus("执行完毕");
            videoStatusRespVo.setEndTime(new Date());
        });
    }
}
