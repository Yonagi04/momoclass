package com.momoclass.content.service;

import com.momoclass.content.model.dto.CoursePreviewDto;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 课程发布
 * @date 2024/03/22 15:02
 */
public interface CoursePublishService {
    /**
     * @description 获取课程预览信息
     * @param courseId 课程 id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     * @author Mr.M
     * @date 2022/9/16 15:36
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 提交课程审核
     * @param courseId
     */
    public void commitAudit(Long courseId, Long companyId);

    public void coursePublish(Long companyId, Long courseId);
}
