package com.momoclass.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 修改课程
 * @date 2024/03/11 13:29
 */
@Data
public class EditCourseDto extends AddCourseDto {
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
}
