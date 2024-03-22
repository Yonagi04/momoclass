package com.momoclass.content.api;

import com.momoclass.content.model.dto.CoursePreviewDto;
import com.momoclass.content.service.CourseBaseInfoService;
import com.momoclass.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description
 * @date 2024/03/22 16:08
 */
@Api(value = "课程公开查询接口",tags = "课程公开查询接口")
@RestController
@RequestMapping("/open")
public class CourseOpenController {
    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId")
                                           Long courseId) {
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo =
                coursePublishService.getCoursePreviewInfo(courseId);
        return coursePreviewInfo;
    }
}
