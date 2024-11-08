package com.qxiaohu.upload.db;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author xiaxh
 * @date 2024/11/7
 */
@KeySequence("law_model_essay_seq")
@Data
public class XiaohuVideo {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 日期
     */
    @TableField("`date`")
    private Date date;

    /**
     * 编号
     */
    private String code;

    /**
     * 地址
     */
    private String url;

    /**
     * 预览
     */
    private String preview;

    /**
     * 图片
     */
    private String img;

    /**
     * 演员
     */
    private String performer;

    /**
     * 时长
     */
    private Integer duration;

    /**
     * 类别
     */
    private String types;

    /**
     * 类别
     */
    private String remark;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 文件名称
     */
    private String file;

}
