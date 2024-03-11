package com.momoclass.content.model.dto;

import lombok.Data;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 新增大章节、小章节，修改章节信息
 * @date 2024/03/11 20:30
 */
@Data
public class SaveTeachplanDto {
    /***
     * 教学计划 id
     */
    private Long id;
    /**
     * 课程计划名称
     */
    private String pname;
    /**
     * 课程计划父级 Id
     */
    private Long parentid;
    /**
     * 层级，分为 1、2、3 级
     */
    private Integer grade;
    /**
     * 课程类型:1 视频、2 文档
     */
    private String mediaType;
    /**
     * 课程标识
     */
    private Long courseId;
    /**
     * 课程发布标识
     */
    private Long coursePubId;
    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;
}
