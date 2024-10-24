package com.qxiaohu.upload.enums;

import com.qxiaohu.upload.exception.ErrorCode;
import com.qxiaohu.upload.exception.GlobalErrorCodeConstants;

/**
 * @author xiaxh
 * @date 2024/6/18
 */
public interface ErrorCodeConstants extends GlobalErrorCodeConstants {

    ErrorCode TASK_CURRENTLY_BEING_EXECUTED = new ErrorCode(100100, "任务正在执行中");
    ErrorCode FILE_PATH_NOT_EXIST = new ErrorCode(100100, "文件路径不正确或不存在");
    ErrorCode FILE_NOT_EXIST = new ErrorCode(100100, "文件不存在");
}
