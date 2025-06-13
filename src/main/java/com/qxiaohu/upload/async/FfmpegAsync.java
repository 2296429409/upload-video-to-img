package com.qxiaohu.upload.async;

import cn.hutool.extra.spring.SpringUtil;
import com.qxiaohu.upload.exception.GlobalErrorCodeConstants;
import com.qxiaohu.upload.pojo.UploadRespVo;
import com.qxiaohu.upload.pojo.VideoReqVo;
import com.qxiaohu.upload.pojo.VideoStatusRespVo;
import com.qxiaohu.upload.util.FfmpegUtil;
import com.qxiaohu.upload.util.ImageUtil;
import com.qxiaohu.upload.util.ServiceExceptionUtil;
import com.qxiaohu.upload.util.UploadFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    @Resource
    private ResourceLoader resourceLoader;

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

        byte[] imgdomeData = new byte[10240];
        int readLen;
        if (StringUtils.hasText(reqVo.getExportFile())) {
            try (FileInputStream fis = new FileInputStream(reqVo.getExportFile())){
                readLen = fis.read(imgdomeData);
            }catch (IOException e){
                throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
            }
        }else {
            try (InputStream fis = resourceLoader.getResource("classpath:static\\1.png").getInputStream();){
                readLen = fis.read(imgdomeData);
            }catch (Exception e){
                throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
            }
        }
        byte[] bytek = ImageUtil.imgKeyStr.getBytes();
        int imgKey = readLen + bytek.length;
        fileNames.forEach(fileName -> {
            VideoStatusRespVo videoStatusRespVo = cache.get(fileName);
            videoStatusRespVo.setStartTime(new Date());
            videoStatusRespVo.setStatus("执行中");
            try {
                //切片
                String m3u8File = FfmpegUtil.run(reqVo.getImportFile() + File.separator + fileName);
                //图片
                String changeImg = ImageUtil.fileChangeImg(m3u8File, imgdomeData, readLen);
                //上传
                Map<String, UploadRespVo> uploadRespVoMap = UploadFileUtil.uploadPhotosExecutor(changeImg, reqVo);
                //重复检查
                UploadFileUtil.uploadPhotosRepeat(uploadRespVoMap,reqVo);
                //图床
                UploadFileUtil.backups(uploadRespVoMap, reqVo);
                //生成m3u8文件
                int err = ImageUtil.setPlayAddress(m3u8File, uploadRespVoMap, imgKey);
                videoStatusRespVo.setStatus("执行完毕,"+err+"个错误");
            videoStatusRespVo.setEndTime(new Date());
            }catch (Exception e){
                videoStatusRespVo.setStatus("执行失败"+e.getMessage());
                videoStatusRespVo.setEndTime(new Date());
            }
        });
    }
}
