package com.xiaohu;

import com.qxiaohu.upload.util.FfmpegUtil;
import org.junit.Test;

import java.io.File;

/**
 * @author xiaxh
 * @date 2024/9/25
 */
public class FfmpegTest {

    @Test
    public void T1(){
//        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
//        System.out.println("ffmpeg = " + ffmpeg);
//
//        String tsFile = FfmpegUtil.videotoTs("D:\\dome\\xiaxh\\hls\\321.mp4");
//        System.out.println("tsFile = " + tsFile);
//
//        String segmentTime = FfmpegUtil.getSegmentTime("D:\\dome\\xiaxh\\hls\\321.mp4");
//        System.out.println("segmentTime = " + segmentTime);
//
//        String m3u8File = FfmpegUtil.videotoM3u8(tsFile, segmentTime);
//        System.out.println("m3u8File = " + m3u8File);


//        System.out.println("= " + FfmpegUtil.run("D:\\dome\\xiaxh\\hls\\321.mp4"));


        FfmpegUtil.findLargeJpgFiles(new File("D:\\dome\\xiaxh\\hls\\321_hls"), 10 * 1024 * 1024);
    }

}
