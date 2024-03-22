package com.momoclass.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;
/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 课程预览模型类
 * @date 2024/03/22 14:50
 */
@Data
@ToString
public class CoursePreviewDto {
    //课程基本信息,课程营销信息
    private CourseBaseInfoDto courseBase;
    //课程计划信息
    private List<TeachplanDto> teachplans;
    //师资信息暂时不加...
}
