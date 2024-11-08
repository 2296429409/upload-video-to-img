package com.xiaohu;

/**
 * @author xiaxh
 * @date 2024/10/24
 */

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileSizeFetcher {

    public static void main(String[] args) {
        // 获取当前目录下的文件夹
        File dir = new File("D:\\dome\\xiaxh\\hls\\321_hls");

        // 创建线程池，指定线程数为3
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 创建一个ConcurrentHashMap存储文件大小
        ConcurrentHashMap<File, Long> fileSizeMap = new ConcurrentHashMap<>();

        // 遍历目录下的所有文件
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                // 只处理文件，排除子目录
                if (file.isFile()) {
                    // 提交任务到线程池
                    executor.submit(() -> {
                        // 存储文件大小到Map
                        fileSizeMap.put(file, file.length());
                    });
                }
            }
        }

        // 关闭线程池，等待所有任务完成
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 输出文件大小
        for (File file : fileSizeMap.keySet()) {
            System.out.println(file.getName() + " 的大小为: " + fileSizeMap.get(file) + " 字节");
        }
    }
}
