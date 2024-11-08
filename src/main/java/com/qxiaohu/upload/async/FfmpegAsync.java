package com.qxiaohu.upload.async;

import com.qxiaohu.upload.pojo.UploadRespVo;
import com.qxiaohu.upload.pojo.VideoReqVo;
import com.qxiaohu.upload.pojo.VideoStatusRespVo;
import com.qxiaohu.upload.util.FfmpegUtil;
import com.qxiaohu.upload.util.ImageUtil;
import com.qxiaohu.upload.util.UploadFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
            //切片
            String m3u8File = FfmpegUtil.run(reqVo.getImportFile() + File.separator + fileName);
            //图片
            String changeImg = ImageUtil.fileChangeImg(m3u8File, reqVo.getExportFile());
            //上传
            Map<String, UploadRespVo> uploadRespVoMap = UploadFileUtil.uploadPhotosExecutor(changeImg, reqVo);
            //图床
            UploadFileUtil.backups(uploadRespVoMap, reqVo);
            //生成m3u8文件
            ImageUtil.setPlayAddress(m3u8File, uploadRespVoMap, reqVo.getExportFile());
            videoStatusRespVo.setStatus("执行完毕");
            videoStatusRespVo.setEndTime(new Date());
        });
    }
}
