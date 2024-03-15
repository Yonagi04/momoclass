package com.momoclass.media.model.dto;

import lombok.Data;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 上传普通文件请求参数
 * @date 2024/03/14 18:26
 */
@Data
public class UploadFileParamsDto {
    // 文件名称
    private String fileName;
    // 文件content-type
    private String contentType;
    // 文件类型
    private String fileType;
    // 文件大小
    private Long fileSize;
    // 文件标签
    private String tags;
    // 上传用户名
    private String username;
    // 备注
    private String remark;
}
