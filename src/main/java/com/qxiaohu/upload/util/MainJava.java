package com.qxiaohu.upload.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class MainJava {

    private static String m3u8file = "\\m3u8";
    private static String imgfile = "\\img";
    private static String imgdome = "\\2.png";
    private static String configfile = "\\config.json";

    //-- 初始 --
    private static String path = "E:\\下载\\m3u8\\run\\upload\\";
//    private static String path = "F:\\av\\M3U8\\up\\";
    private static String server = "https://gd-hbimg.huaban.com/%s?key=%s";
    private static int imgKey = 0;
    private static String imgKeyStr = "\n---data---\n";
    //-- 初始 --

    public static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
    public static ConcurrentHashMap<String, Integer> mapid = new ConcurrentHashMap<String, Integer>();
    public static ConcurrentHashMap<String, String> repeatMap = new ConcurrentHashMap<String, String>();

    private static JSONObject configJson = null;

    static {
//        path = System.getProperty("user.dir");
        m3u8file = path + m3u8file;
        imgfile = path + imgfile;
        imgdome = path + imgdome;
        configfile = path + configfile;
        File file = new File(configfile);
        try {
            FileReader fileReader = new FileReader(file);
            char[] chars = new char[2048];
            int read = fileReader.read(chars);
            configJson = JSON.parseObject(new String(chars, 0, read));
            server = configJson.get("server").toString();
        } catch (Exception e) {
            System.out.println("0.json读取错误");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try(FileInputStream fis = new FileInputStream(new File(imgdome));){
            byte[] bytes = new byte[10240];
            int read = fis.read(bytes);
            byte[] bytek = imgKeyStr.getBytes();
            imgKey = read + bytek.length;
            System.out.println("imgKey = " + imgKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        fileChangeImg();
        uploadPhotos();
//        uploadPhotosTwo();
        setPlayAddress();
        backups();

        try {
            System.out.println(" 执行完毕 ······································ ");
            Thread.sleep(500000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改文件地址
     */
    private static void setPlayAddress() {
        File file = new File(m3u8file);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < file.listFiles().length; i++) {
            File tsFile = file.listFiles()[i];
            if (tsFile.getName().indexOf(".m3u8") != -1) {
                try (BufferedReader br = new BufferedReader(new FileReader(tsFile))) {
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        if (line.indexOf(".ts") != -1) {
                            String nameTs = line.substring(0, line.lastIndexOf("."));
                            String str = nameTs + " : null";
                            try {
                                str = URLEncoder.encode(map.get(nameTs), "UTF-8");
                                line = String.format(server, str, imgKey);
                            } catch (Exception e) {
                                System.out.println("4.错误数据 = " + nameTs);
                            }
                        }
                        data.add(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path + "\\my.m3u8")))) {
            for (int i = 0; i < data.size(); i++) {
                bw.write(data.get(i));
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件加密转img
     */
    private static void fileChangeImg() {
        File file = new File(m3u8file);
        for (int i = 0; i < file.listFiles().length; i++) {
            File tsFile = file.listFiles()[i];
            if (tsFile.getName().indexOf(".m3u8") == -1) {
                System.out.println("1.正在处理文件: " + tsFile.getName());
                String nameTs = tsFile.getName().substring(0, tsFile.getName().lastIndexOf("."));
                byte[] bytek = imgKeyStr.getBytes();
                try (
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(imgfile).getPath() + "/" + nameTs + ".jpg"));
                        FileInputStream fis = new FileInputStream(new File(imgdome));
                        final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(tsFile));
                ) {
                    byte[] bytes = new byte[10240];
                    int read = fis.read(bytes);
                    bos.write(bytes, 0, read);
                    bos.flush();
                    bos.write(bytek);
                    bos.flush();
                    byte[] buff = new byte[1024];
                    int bytesRead = 0;
                    while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                        bos.write(buff, 0, bytesRead);
                        bos.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 上传图片
     */
    private static void uploadPhotos() {
        String cookie = configJson.get("cookie").toString();
        String auth_token = configJson.get("auth_token").toString();
        Integer executorSize = Integer.parseInt(configJson.get("executorSize").toString());
        File imgFile = new File(imgfile);
        File[] files = imgFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            while (mapid.size() < i - executorSize + 1) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            Thread thread = new Thread(new UpImgOneRunable(cookie, auth_token, files[i].getPath()));
//            thread.start();
            System.out.println("2.开始上传: " + files[i].getName());
        }
        while (mapid.size() != files.length) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重复检查
     */
    private static void uploadPhotosTwo() {
        repeatMap.clear();
        List<String> upload = MainJava.map.entrySet().stream().filter(entry -> entry.getValue().equals("Exception")).map(Map.Entry::getKey).collect(Collectors.toList());
        String cookie = configJson.get("cookie").toString();
        String auth_token = configJson.get("auth_token").toString();
        Integer executorSize = Integer.parseInt(configJson.get("executorSize").toString());
        for (int i = 0; i < upload.size(); i++) {
            MainJava.map.put(upload.get(i), "uploadPhotosTwo");
            File file = new File(m3u8file + "\\" + upload.get(i) + ".ts");
            while (repeatMap.size() < i - executorSize + 1) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            Thread thread = new Thread(new UpImgOneRunable(cookie, auth_token, file.getPath()));
//            thread.start();
            System.out.println("3.开始重复上传: " + file.getName());
        }
        while (repeatMap.size() != upload.size()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 保存到床图
     */
    public static void backups() {
        List<Integer> id = mapid.values().stream().collect(Collectors.toList());
        int count = (int) Math.ceil(id.size() / 20.0);
        for (int i = 0; i < count; i++) {
            List<Map<String, Object>> pins = new ArrayList<>();
            List<Integer> idtem = id.stream().skip(i * 20).limit(20).collect(Collectors.toList());
            idtem.forEach(x -> {
                Map<String, Object> map = new HashMap<>();
                map.put("file_id", x);
                pins.add(map);
            });
            backupsApi(pins);
        }
    }

    private static void backupsApi(List<Map<String, Object>> pins) {
        Map<String, Object> map = new HashMap<>();
        map.put("board_id", Integer.parseInt(configJson.get("board_id").toString()));
        map.put("pins", pins);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(map));
        Request request = new Request.Builder()
                .url("https://api.huaban.com/pins/batch")
                .method("POST", body)
                .addHeader("Cookie", configJson.get("cookie").toString())
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println("5.保存结果 = " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getM3u8file() {
        return m3u8file;
    }

    public static String getImgfile() {
        return imgfile;
    }

    public static String getImgdome() {
        return imgdome;
    }

    public static String getConfigfile() {
        return configfile;
    }

    public static String getPath() {
        return path;
    }

    public static String getServer() {
        return server;
    }

    public static int getImgKey() {
        return imgKey;
    }

    public static String getImgKeyStr() {
        return imgKeyStr;
    }

    public static ConcurrentHashMap<String, String> getMap() {
        return map;
    }

    public static ConcurrentHashMap<String, Integer> getMapid() {
        return mapid;
    }

    public static ConcurrentHashMap<String, String> getRepeatMap() {
        return repeatMap;
    }

    public static JSONObject getConfigJson() {
        return configJson;
    }
}
