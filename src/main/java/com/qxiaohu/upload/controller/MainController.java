package com.qxiaohu.upload.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qxiaohu.upload.db.XiaohuVideo;
import com.qxiaohu.upload.pojo.*;
import com.qxiaohu.upload.service.MainService;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.qxiaohu.upload.pojo.CommonResult.success;

/**
 * 控制器
 * @author xiaxh
 * @date 2024/6/17
 */
@RestController
@RequestMapping("/api")
@Validated
public class MainController {

    @Resource
    private MainService service;

    private static final int BATCH_SIZE = 20; // 每个文件包含的数据条数
    private static final String BASE_URL = "https://2296429409.github.io/app_m3u8/"; // 基础URL

    @PostMapping("/word")
    public CommonResult<List<String>> execWordTask(@Valid @RequestBody VideoReqVo reqVo) {
        return success(service.startWordTask(reqVo));
    }

    @GetMapping("/status")
    public CommonResult<List<VideoStatusRespVo>> status() {
        return success(service.status());
    }

    @PostMapping("/save")
    public CommonResult<List<XiaohuVideo>> save(@RequestBody VideoImportVo vo) {
        return success(service.save(vo.getPath()));
    }

    @GetMapping("/page")
    public CommonResult<Page<XiaohuVideo>> page(String name, Long page, Long size) {
        return success(service.page(name,page,size));
    }

    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> delete(@PathVariable Long id) {
        return success(service.removeById(id));
    }

    @PostMapping("/add")
    public CommonResult<Boolean> add(@RequestBody XiaohuVideo video) {
        return success(service.save(video));
    }

    @PutMapping("/update")
    public CommonResult<Boolean> update(@RequestBody XiaohuVideo video) {
        return success(service.updateById(video));
    }

    @GetMapping("/export")
    public void exportJson(HttpServletResponse response) throws IOException {
        // 获取所有数据
        List<XiaohuVideo> allVideos = service.page(null, 1L,Long.MAX_VALUE).getRecords();
        
        // 创建ZIP输出流
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=videos.zip");
        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());

        // 计算需要多少个文件
        int totalFiles = (allVideos.size() + BATCH_SIZE - 1) / BATCH_SIZE;
        
        // 创建page.json的URL列表
        JSONObject pageJson = new JSONObject();
        JSONArray urlArray = new JSONArray();
        
        // 分批处理数据
        for (int i = 0; i < totalFiles; i++) {
            // 获取当前批次的数据
            int fromIndex = i * BATCH_SIZE;
            int toIndex = Math.min(fromIndex + BATCH_SIZE, allVideos.size());
            List<XiaohuVideo> batchVideos = allVideos.subList(fromIndex, toIndex);
            
            // 创建JSON对象
            JSONObject root = new JSONObject();
            List<VideoInfo> videoInfos = new ArrayList<>();
            for (XiaohuVideo demoDo2 : batchVideos) {
                if (StringUtils.hasText(demoDo2.getName())){
                    VideoInfo videoInfo = new VideoInfo();
                    videoInfo.setCode(demoDo2.getCode());
                    videoInfo.setName(demoDo2.getName());
                    videoInfo.setDate(demoDo2.getDate());
                    videoInfo.setPerformer(demoDo2.getPerformer());
                    videoInfo.setUrl(demoDo2.getUrl());
                    videoInfo.setImg(demoDo2.getImg());
                    videoInfo.setPreview(
                            new ArrayList<>(
                                    Arrays.asList(demoDo2.getPreview().split("、"))));
                    videoInfos.add(videoInfo);
                }
            }
            root.put("data", videoInfos);
            
            // 创建分片JSON文件
            String fileName = String.format("list%d.json", i + 1);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            
            // 写入JSON数据
            byte[] jsonBytes = JSON.toJSONString(root).getBytes(StandardCharsets.UTF_8);
            zipOut.write(jsonBytes);
            zipOut.closeEntry();
            
            // 添加到URL列表
            urlArray.add(BASE_URL + fileName);
        }
        
        // 创建并写入page.json
        pageJson.put("data", urlArray);
        ZipEntry pageEntry = new ZipEntry("page.json");
        zipOut.putNextEntry(pageEntry);
        byte[] pageJsonBytes = JSON.toJSONString(pageJson).getBytes(StandardCharsets.UTF_8);
        zipOut.write(pageJsonBytes);
        zipOut.closeEntry();
        
        zipOut.close();
    }

}
