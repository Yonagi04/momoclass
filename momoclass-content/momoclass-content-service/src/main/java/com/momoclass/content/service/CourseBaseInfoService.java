package com.momoclass.content.service;

import com.momoclass.base.model.PageParams;
import com.momoclass.base.model.PageResult;
import com.momoclass.content.model.dto.AddCourseDto;
import com.momoclass.content.model.dto.CourseBaseInfoDto;
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

    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);
}
