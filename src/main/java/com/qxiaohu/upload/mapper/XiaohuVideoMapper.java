package com.qxiaohu.upload.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qxiaohu.upload.db.XiaohuVideo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.StringUtils;


/**
 * @author xiaxh
 * @date 2024/11/7
 */
@Mapper
public interface XiaohuVideoMapper extends BaseMapper<XiaohuVideo> {
    default Page<XiaohuVideo> page(String name, Long page, Long size){
        return selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<XiaohuVideo>()
                        .and(StringUtils.hasText(name), q->q.like( XiaohuVideo::getName, name).or()
                                .like(XiaohuVideo::getCode, name).or()
                                .like(XiaohuVideo::getRemark, name).or()
                                .like(XiaohuVideo::getPerformer, name).or()
                                .like(XiaohuVideo::getTypes, name))
                        .orderByDesc(XiaohuVideo::getDate));
    }
}
