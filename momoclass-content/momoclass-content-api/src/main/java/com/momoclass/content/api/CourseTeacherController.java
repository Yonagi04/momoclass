package com.momoclass.content.api;

import com.momoclass.content.model.po.CourseTeacher;
import com.momoclass.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 教师信息接口类
 * @date 2024/03/12 15:52
 */
@RestController
@Slf4j
@Api(value = "教师信息相关接口", tags = "教师信息相关接口")
public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("查询教师信息接口")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeacher(@PathVariable Long courseId) {
        return courseTeacherService.getCourseTeacher(courseId);
    }

    @ApiOperation("添加/修改教师信息接口")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        return courseTeacherService.saveCourseTeacher(courseTeacher);
    }

    @ApiOperation("删除教师信息接口")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        courseTeacherService.deleteCourseTeacher(courseId, teacherId);
    }
}
