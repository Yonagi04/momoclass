package com.momoclass.content.model.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Yonagi
 * @date 2024/3/8
 * @version 1.0
 * @description 课程查询条件模型类
 */
public class QueryCourseParamsDto {
    @ApiModelProperty("审核状态")
    private String auditStatus;
    @ApiModelProperty("课程名称")
    private String courseName;
    @ApiModelProperty("发布状态")
    private String publishStatus;
}
