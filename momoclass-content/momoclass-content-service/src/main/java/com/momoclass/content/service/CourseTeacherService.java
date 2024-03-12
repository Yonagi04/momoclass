package com.momoclass.content.service;

import com.momoclass.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 教师信息接口服务类
 * @date 2024/03/12 16:09
 */
public interface CourseTeacherService {

    public List<CourseTeacher> getCourseTeacher(Long courseId);

    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher);

    public void deleteCourseTeacher(Long courseId, Long teacherId);
}
