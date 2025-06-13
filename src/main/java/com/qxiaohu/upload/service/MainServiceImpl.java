package com.qxiaohu.upload.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qxiaohu.upload.db.XiaohuVideo;
import com.qxiaohu.upload.enums.ErrorCodeConstants;
import com.qxiaohu.upload.mapper.XiaohuVideoMapper;
import com.qxiaohu.upload.pojo.VideoReqVo;
import com.qxiaohu.upload.pojo.VideoStatusRespVo;
import com.qxiaohu.upload.util.JavtrailersUtil;
import com.qxiaohu.upload.async.FfmpegAsync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    @Resource
    private XiaohuVideoMapper videoMapper;

    @Override
    public synchronized List<String> startWordTask(VideoReqVo reqVo) {
        if (!cache.isEmpty()) {
            throw exception(ErrorCodeConstants.TASK_CURRENTLY_BEING_EXECUTED);
        }
        File importFile = new File(reqVo.getImportFile());
        if (StringUtils.hasText(reqVo.getExportFile()) && !new File(reqVo.getExportFile()).isFile()){
            throw exception(ErrorCodeConstants.FILE_PATH_NOT_EXIST);
        }
        if (importFile.isDirectory()) {
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

    @Override
    @Transactional
    public List<XiaohuVideo> save(String filePath) {
        File path = new File(filePath);
        File[] files = path.listFiles(pathname -> pathname.getName().endsWith(".m3u8"));
        if (files == null){
            return new ArrayList<>();
        }
        List<XiaohuVideo> list = new ArrayList<>();
        Date date = new Date();
        for (File file : files) {
            String fileName = file.getName().replace(".m3u8", "");
            String contentId = JavtrailersUtil.getVideoLike(fileName);
            XiaohuVideo xiaohuVideo = new XiaohuVideo();
            xiaohuVideo.setRemark(contentId);
            xiaohuVideo.setFile(file.getName());
            JavtrailersUtil.getVideoInfo(xiaohuVideo);
            xiaohuVideo.setUpdateTime(date);
            xiaohuVideo.setUrl("https://2296429409.github.io/app_m3u8/"+file.getName());
            videoMapper.insert(xiaohuVideo);
            list.add(xiaohuVideo);
        }
        return list;
    }

    @Override
    public Page<XiaohuVideo> page(String name, Long page, Long size) {
        return videoMapper.page(name, page, size);
    }

    @Override
    public boolean removeById(Long id) {
        return videoMapper.deleteById(id) > 0;
    }

    @Override
    public boolean save(XiaohuVideo video) {
        video.setUpdateTime(new Date());
        return videoMapper.insert(video) > 0;
    }

    @Override
    public boolean updateById(XiaohuVideo video) {
        video.setUpdateTime(new Date());
        return videoMapper.updateById(video) > 0;
    }

}
