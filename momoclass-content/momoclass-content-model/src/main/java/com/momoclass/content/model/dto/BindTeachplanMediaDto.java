package com.momoclass.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 媒体文件绑定提交数据
 * @date 2024/03/21 18:26
 */
@Data
@ApiModel(value = "BindTeachplanMediaDto", description = "媒体文件绑定提交数据")
public class BindTeachplanMediaDto {
    @ApiModelProperty(value = "媒体文件id", required = true)
    private String mediaId;
    @ApiModelProperty(value = "媒体文件名称", required = true)
    private String fileName;
    @ApiModelProperty(value = "课程计划标识", required = true)
    private Long teachplanId;
}
