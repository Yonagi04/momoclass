package com.momoclass.content.api;

import com.momoclass.base.model.PageParams;
import com.momoclass.base.model.PageResult;
import com.momoclass.content.model.dto.QueryCourseParamsDto;
import com.momoclass.content.model.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yonagi
 * @date 2024/3/8
 * @version 1.0
 * @description 分页查询controller层
 */
@Api(value = "课程信息管理接口", tags = "课程信息管理接口")
@RestController
public class CourseBaseInfoController {
    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        CourseBase courseBase = new CourseBase();
        courseBase.setName("测试名称");
        courseBase.setCreateDate(LocalDateTime.now());
        List<CourseBase> courseBases = new ArrayList<>();
        courseBases.add(courseBase);
        PageResult pageResult = new PageResult<CourseBase>(courseBases, 10L, 1L, 10L);
        return pageResult;
    }
}
