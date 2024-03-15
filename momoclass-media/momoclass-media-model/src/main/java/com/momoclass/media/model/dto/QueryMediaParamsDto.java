package com.momoclass.media.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 媒体文件查询请求模型类
 * @date 2024/03/14 10:59
 */
@Data
@ToString
public class QueryMediaParamsDto {
    @ApiModelProperty("媒体文件名称")
    private String fileName;
    @ApiModelProperty("媒体文件类型")
    private String fileType;
    @ApiModelProperty("审核状态")
    private String auditStatus;
}
