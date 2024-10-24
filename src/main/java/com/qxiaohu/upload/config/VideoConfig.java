package com.qxiaohu.upload.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author xiaxh
 * @date 2024/9/12
 */
@Data
public class VideoConfig {

    @Value("${video.ffmpeg}")
    private String ffmpeg;
}
