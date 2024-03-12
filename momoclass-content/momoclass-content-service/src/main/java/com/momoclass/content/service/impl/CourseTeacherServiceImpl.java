package com.momoclass.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.momoclass.base.exception.MomoClassException;
import com.momoclass.content.mapper.CourseTeacherMapper;
import com.momoclass.content.model.po.CourseTeacher;
import com.momoclass.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 教师信息接口服务类实现
 * @date 2024/03/12 16:09
 */
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> getCourseTeacher(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    @Override
    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher) {
        Long id = courseTeacher.getId();
        if (id == null) {
            // 不存在，新建一个新的教师信息
            CourseTeacher teacher = new CourseTeacher();
            BeanUtils.copyProperties(courseTeacher, teacher);
            teacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(teacher);
            if (insert <= 0) {
                MomoClassException.cast("新增教师信息失败");
            }
            return getCourseTeacher(teacher);
        } else {
            // 存在，保存新的教师信息
            CourseTeacher teacher = courseTeacherMapper.selectById(id);
            BeanUtils.copyProperties(courseTeacher, teacher);
            int update = courseTeacherMapper.updateById(teacher);
            if (update <= 0) {
                MomoClassException.cast("修改教师信息失败");
            }
            return getCourseTeacher(teacher);
        }
    }

    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId)
                .eq(CourseTeacher::getId, teacherId);
        int delete = courseTeacherMapper.delete(queryWrapper);
        if (delete <= 0) {
            MomoClassException.cast("删除教师信息失败");
        }
    }

    private CourseTeacher getCourseTeacher(CourseTeacher teacher) {
        return courseTeacherMapper.selectById(teacher.getId());
    }
}
