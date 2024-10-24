package com.qxiaohu.upload.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.qxiaohu.upload.config.UploadFileConfig;
import com.qxiaohu.upload.pojo.CommonResult;
import com.qxiaohu.upload.pojo.VideoReqVo;
import com.qxiaohu.upload.pojo.VideoStatusRespVo;
import com.qxiaohu.upload.service.MainService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.qxiaohu.upload.pojo.CommonResult.success;

/**
 * 控制器
 * @author xiaxh
 * @date 2024/6/17
 */
@RestController
@RequestMapping("/api")
@Validated
public class MainController {

    @Resource
    private MainService service;

    @GetMapping("/config")
    public CommonResult<List<String>> config() {
        Map<String, UploadFileConfig> beansOfType = SpringUtil.getBeansOfType(UploadFileConfig.class);
        return success(beansOfType.values().stream().map(UploadFileConfig::getName).collect(Collectors.toList()));
    }

    @PostMapping("/word")
    public CommonResult<List<String>> execWordTask(@Valid @RequestBody VideoReqVo reqVo) {
        return success(service.startWordTask(reqVo));
    }

    @GetMapping("/status")
    public CommonResult<List<VideoStatusRespVo>> status() {
        return success(service.status());
    }

}
