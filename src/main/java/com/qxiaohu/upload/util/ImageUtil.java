package com.qxiaohu.upload.util;

import cn.hutool.extra.spring.SpringUtil;
import com.qxiaohu.upload.exception.GlobalErrorCodeConstants;
import com.qxiaohu.upload.pojo.UploadRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiaxh
 * @date 2024/10/24
 */
@Slf4j
public class ImageUtil {

    public static String imgKeyStr = "\n---data---\n";
    public static String server = "https://gd-hbimg.huaban.com/%s?key=%s";

    /**
     * 文件加密转img
     */
    public static String fileChangeImg(String m3u8file, byte[] imgdomeData, int readLen) {
        File file = new File(m3u8file);
        File[] files = file.getParentFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".ts"));
        if (files == null){
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        byte[] bytek = imgKeyStr.getBytes();
        File imgFile = new File(file.getParent() + File.separator + "_img");
        if (!imgFile.exists()) {
            imgFile.mkdir();
        }
        for (File tsFile : files) {
            log.info("文件加密转img: " + tsFile.getName());
            String nameTs = tsFile.getName().substring(0, tsFile.getName().lastIndexOf("."));
            try (
                    BufferedOutputStream bos = new BufferedOutputStream(
                            Files.newOutputStream(Paths.get(imgFile.getPath() + File.separator + nameTs + ".jpg"))
                    );
                    final BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(tsFile.toPath()));
            ) {
                bos.write(imgdomeData, 0, readLen);
                bos.flush();
                bos.write(bytek);
                bos.flush();
                byte[] buff = new byte[1024];
                int bytesRead;
                while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                    bos.write(buff, 0, bytesRead);
                    bos.flush();
                }
            } catch (Exception e) {
                throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
            }
        }
        return imgFile.getPath();
    }
    /**
     * 文件加密转img
     */
    public static String fileChangeImg(String m3u8file, String imgdome) {
        File file = new File(m3u8file);
        File[] files = file.getParentFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".ts"));
        if (files == null){
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        byte[] imgdomeData = new byte[10240];
        int readLen;
        try (FileInputStream fis = new FileInputStream(imgdome)){
            readLen = fis.read(imgdomeData);
        }catch (IOException e){
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        byte[] bytek = imgKeyStr.getBytes();
        File imgFile = new File(file.getParent() + File.separator + "_img");
        if (!imgFile.exists()) {
            imgFile.mkdir();
        }
        for (File tsFile : files) {
            log.info("文件加密转img: " + tsFile.getName());
            String nameTs = tsFile.getName().substring(0, tsFile.getName().lastIndexOf("."));
            try (
                    BufferedOutputStream bos = new BufferedOutputStream(
                            Files.newOutputStream(Paths.get(imgFile.getPath() + File.separator + nameTs + ".jpg"))
                    );
                    final BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(tsFile.toPath()));
            ) {
                bos.write(imgdomeData, 0, readLen);
                bos.flush();
                bos.write(bytek);
                bos.flush();
                byte[] buff = new byte[1024];
                int bytesRead;
                while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                    bos.write(buff, 0, bytesRead);
                    bos.flush();
                }
            } catch (Exception e) {
                throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
            }
        }
        return imgFile.getPath();
    }



    /**
     * 修改文件地址
     */
    public static int setPlayAddress(String m3u8File, Map<String, UploadRespVo> map, String imgdome) {
        int err = 0;
        int imgKey = 0;
        try (FileInputStream fis = new FileInputStream(imgdome)){
            byte[] bytes = new byte[10240];
            int read = fis.read(bytes);
            byte[] bytek = imgKeyStr.getBytes();
            imgKey = read + bytek.length;
        }catch (Exception e){
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        List<String> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(m3u8File))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.contains(".ts")) {
                    String nameTs = line.substring(0, line.lastIndexOf("."));
                    String str;
                    try {
                        str = URLEncoder.encode(map.get(nameTs).getKey(), "UTF-8");
                        line = String.format(server, str, imgKey);
                    } catch (Exception e) {
                        log.error("设置m3u8地址，错误数据 = {}", nameTs);
                        line += " -错误数据";
                        err ++ ;
                    }
                }
                data.add(line);
            }
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        File file = new File(m3u8File);
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(
                        file.getParentFile().getParent() + File.separator +name+ "_to.m3u8"))) {
            for (String datum : data) {
                bw.write(datum);
                bw.newLine();
            }
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        return err;
    }



    /**
     * 修改文件地址
     */
    public static int setPlayAddress(String m3u8File, Map<String, UploadRespVo> map, int imgKey) {
        int err = 0;
//        int imgKey = 0;
//        try (FileInputStream fis = new FileInputStream(imgdome)){
//            byte[] bytes = new byte[10240];
//            int read = fis.read(bytes);
//            byte[] bytek = imgKeyStr.getBytes();
//            imgKey = read + bytek.length;
//        }catch (Exception e){
//            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
//        }
        List<String> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(m3u8File))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.contains(".ts")) {
                    String nameTs = line.substring(0, line.lastIndexOf("."));
                    String str;
                    try {
                        str = URLEncoder.encode(map.get(nameTs).getKey(), "UTF-8");
                        line = String.format(server, str, imgKey);
                    } catch (Exception e) {
                        log.error("设置m3u8地址，错误数据 = {}", nameTs);
                        line += " -错误数据";
                        err ++ ;
                    }
                }
                data.add(line);
            }
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        File file = new File(m3u8File);
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(
                        file.getParentFile().getParent() + File.separator +name+ "_to.m3u8"))) {
            for (String datum : data) {
                bw.write(datum);
                bw.newLine();
            }
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        return err;
    }

}
