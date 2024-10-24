package com.qxiaohu.upload.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.qxiaohu.upload.pojo.UploadRespVo;
import com.qxiaohu.upload.pojo.VideoReqVo;
import com.qxiaohu.upload.exception.GlobalErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xiaxh
 * @date 2024/7/12
 */
@Slf4j
public class UploadFileUtil {

    /**
     * 上传图片
     */
    public static Map<String, UploadRespVo> uploadPhotosExecutor(String filePath, VideoReqVo config) {
        // 获取当前目录下的文件夹
        File dir = new File(filePath);
        // 创建线程池，指定线程数为3
        ExecutorService executor = Executors.newFixedThreadPool(config.getExecutorSize());
        // 创建一个ConcurrentHashMap存储
        ConcurrentHashMap<String, UploadRespVo> dataMap = new ConcurrentHashMap<>();
        // 遍历目录下的所有文件
        File[] files = dir.listFiles(
                pathname -> pathname.isFile()
                        && pathname.getName().endsWith(".jpg"));
        if (files != null) {
            for (File file : files) {
                // 提交任务到线程池
                executor.submit(() -> {
                    // 存储文件到Map
                    uploadPhotos(file, dataMap, config);
                });
            }
        }
        // 关闭线程池，等待所有任务完成
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        JsonUtil.toFile(dataMap, dir.getPath() + File.separator + "upload.json");
        return dataMap;
    }

    /**
     * 上传图片
     */
    private static void uploadPhotos(File file, Map<String, UploadRespVo> dataMap, VideoReqVo config) {
        log.info("开始上传: " + file.getName());
        String name = file.getName();
        String nameTs = name.substring(0, name.lastIndexOf("."));
        UploadRespVo uploadRespVo = new UploadRespVo();
        uploadRespVo.setResult(false);
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .readTimeout(60000, TimeUnit.MILLISECONDS)
                    .build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getPath(),
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    file))
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.huaban.com/upload")
                    .method("POST", body)
                    .addHeader("Cookie", config.getCookie())
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject jsonResponse = JSON.parseObject(response.body().string());
            uploadRespVo.setId(jsonResponse.getInteger("id"));
            uploadRespVo.setKey(jsonResponse.getString("key"));
            uploadRespVo.setResult(true);
        } catch (Exception e) {
            log.error("上传错误：" + name);
        } finally {
            dataMap.put(nameTs, uploadRespVo);
        }
    }


    /**
     * 保存到床图
     */
    public static void backups(Map<String, UploadRespVo> dataMap, VideoReqVo config) {
        List<Integer> id = dataMap.values().stream().map(UploadRespVo::getId).collect(Collectors.toList());
        int count = (int) Math.ceil(id.size() / 20.0);
        for (int i = 0; i < count; i++) {
            List<Map<String, Object>> pins = new ArrayList<>();
            List<Integer> idtem = id.stream().skip(i * 20L).limit(20).collect(Collectors.toList());
            idtem.forEach(x -> {
                Map<String, Object> map = new HashMap<>();
                map.put("file_id", x);
                pins.add(map);
            });
            backupsApi(pins, config);
        }
    }

    private static void backupsApi(List<Map<String, Object>> pins, VideoReqVo config) {
        Map<String, Object> map = new HashMap<>();
        map.put("board_id", config.getBoardId());
        map.put("pins", pins);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(map));
        Request request = new Request.Builder()
                .url("https://api.huaban.com/pins/batch")
                .method("POST", body)
                .addHeader("Cookie", config.getCookie())
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            log.info("图库保存结果：" + response);
        } catch (IOException e) {
            log.error("图库保存失败", e);
        }
    }


}
