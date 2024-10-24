package com.qxiaohu.upload.util;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * @author xiaxh
 * @date 2024/10/24
 */
@Slf4j
public class JsonUtil {

    public static void toFile(Object o, String filePath) {
        String jsonString = JSON.toJSONString(o);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(jsonString);
        } catch (Exception e) {
            log.error("write json file error", e);
        }
    }


    public static void toJson(String json, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(json);
        } catch (Exception e) {
            log.error("write json file error", e);
        }
    }
}
