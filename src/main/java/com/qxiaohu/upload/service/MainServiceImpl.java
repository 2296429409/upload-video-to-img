package com.qxiaohu.upload.service;

import com.qxiaohu.upload.enums.ErrorCodeConstants;
import com.qxiaohu.upload.pojo.VideoReqVo;
import com.qxiaohu.upload.pojo.VideoStatusRespVo;
//import com.huaweisoft.hwchatgpt.word.AsyncMain;
import com.qxiaohu.upload.word.FfmpegAsync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.qxiaohu.upload.util.ServiceExceptionUtil.exception;

/**
 * @author xiaxh
 * @date 2024/6/17
 */
@Service
@Slf4j
public class MainServiceImpl implements MainService {

    //文件处理列表
    private final static ConcurrentHashMap<String, VideoStatusRespVo> cache = new ConcurrentHashMap<>();

    @Resource
    private FfmpegAsync asyncMain;

    @Override
    public synchronized List<String> startWordTask(VideoReqVo reqVo) {
        if (!cache.isEmpty()) {
            throw exception(ErrorCodeConstants.TASK_CURRENTLY_BEING_EXECUTED);
        }
        File importFile = new File(reqVo.getImportFile());
        File exportFile = new File(reqVo.getExportFile());
        if (importFile.isDirectory() && exportFile.isFile()) {
            //获取文件列表
            File[] docFiles = importFile.listFiles(
                    pathname -> pathname.isFile()
                            && !pathname.getName().startsWith("~$")
                            && pathname.getName().endsWith(".mp4"));
            if (docFiles == null || docFiles.length == 0){
                throw exception(ErrorCodeConstants.FILE_NOT_EXIST);
            }
            //缓存数据
            for (File file : docFiles) {
                VideoStatusRespVo videoStatusRespVo = new VideoStatusRespVo();
                videoStatusRespVo.setFileName(file.getName());
                videoStatusRespVo.setStatus("待执行");
                videoStatusRespVo.setTaskParams(reqVo);
                cache.put(file.getName(), videoStatusRespVo);
            }
            // 异步执行
            asyncMain.execVideoTask(reqVo, cache);
            return Arrays.stream(docFiles).map(File::getName).collect(Collectors.toList());
        } else {
            throw exception(ErrorCodeConstants.FILE_PATH_NOT_EXIST);
        }
    }


    @Override
    public List<VideoStatusRespVo> status() {
        if (cache.isEmpty()) {
            return null;
        }
        List<VideoStatusRespVo> collect = cache.values()
                .stream()
                //开始时间排序
                .sorted((o1, o2) -> {
                    if (o1.getStartTime() == null) {
                        return 1;
                    } else if (o2.getStartTime() == null) {
                        return -1;
                    }
                    return o2.getStartTime().compareTo(o1.getStartTime());
                })
                //结束时间排序
                .sorted((o1, o2) -> {
                    if (o1.getEndTime() == null) {
                        return 1;
                    } else if (o2.getEndTime() == null) {
                        return -1;
                    }
                    return o2.getEndTime().compareTo(o1.getEndTime());
                })
                .collect(Collectors.toList());
        if (collect.get(collect.size() - 1).getEndTime() != null) {
            //清除数据
            cache.clear();
        }
        return collect;
    }


}
