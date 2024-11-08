package com.qxiaohu.upload.pojo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author xiaxh
 * @date 2024/10/25
 */
@Data
public class VideoInfo {

    private List<String> preview;
    private String name;
    private String code;
    @JSONField(format = "yyyy-MM-dd")
    private Date date;
    private String performer;
    private String url;
    private String img;

}
