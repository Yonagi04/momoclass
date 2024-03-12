package com.momoclass.content.service;

import com.momoclass.base.model.PageParams;
import com.momoclass.base.model.PageResult;
import com.momoclass.content.model.dto.AddCourseDto;
import com.momoclass.content.model.dto.CourseBaseInfoDto;
import com.momoclass.content.model.dto.EditCourseDto;
import com.momoclass.content.model.dto.QueryCourseParamsDto;
import com.momoclass.content.model.po.CourseBase;

/**
 * @author Yonagi
 * @date 2024/3/9
 * @version 1.1
 * @description 课程信息管理接口
 */
public interface CourseBaseInfoService {
    /**
     * 课程分页查询
     * @param pageParams
     * @param courseParamsDto
     * @return 返回课程结果
     */
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto);

    /**
     * 新增课程
     * @param companyId
     * @param addCourseDto
     * @return
     */
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 按id查询课程
     * @param courseId
     * @return
     */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 修改课程
     * @param editCourseDto
     * @return
     */
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

    /**
     * 删除课程
     * @param courseId
     */
    public void deleteCourse(Long companyId, Long courseId);
}
