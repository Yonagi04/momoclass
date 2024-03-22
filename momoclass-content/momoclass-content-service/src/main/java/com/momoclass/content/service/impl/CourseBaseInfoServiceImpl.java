package com.momoclass.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.momoclass.base.exception.MomoClassException;
import com.momoclass.base.model.PageParams;
import com.momoclass.base.model.PageResult;
import com.momoclass.content.mapper.*;
import com.momoclass.content.model.dto.AddCourseDto;
import com.momoclass.content.model.dto.CourseBaseInfoDto;
import com.momoclass.content.model.dto.EditCourseDto;
import com.momoclass.content.model.dto.QueryCourseParamsDto;
import com.momoclass.content.model.po.*;
import com.momoclass.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yonagi
 * @date 2024/3/9
 * @version 1.0
 * @description 课程信息管理接口
 */
@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Autowired
    TeachplanMapper teachplanMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        // 课程名称模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()), CourseBase::getName,
                courseParamsDto.getCourseName());
        // 课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()), CourseBase::getAuditStatus,
                courseParamsDto.getAuditStatus());
        // 课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()), CourseBase::getStatus,
                courseParamsDto.getPublishStatus());
        // 创建page分页参数对象，参数，当前页码，每页记录数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 分页查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        // 数据列表
        List<CourseBase> items = pageResult.getRecords();
        // 总记录数
        long total = pageResult.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<CourseBase>(items, total,
                pageParams.getPageNo(), pageParams.getPageSize());
        return courseBasePageResult;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        // 参数合法性校验
        if (StringUtils.isBlank(addCourseDto.getName())) {
//            throw new RuntimeException("课程名称为空");
            MomoClassException.cast("课程名称为空");
        }
        if (StringUtils.isBlank(addCourseDto.getMt())) {
//            throw new RuntimeException("课程分类为空");
            MomoClassException.cast("课程分类为空");
        }
        if (StringUtils.isBlank(addCourseDto.getGrade())) {
//            throw new RuntimeException("课程等级为空");
            MomoClassException.cast("课程等级为空");
        }
        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
//            throw new RuntimeException("教育模式为空");
            MomoClassException.cast("教育模式为空");
        }
        if (StringUtils.isBlank(addCourseDto.getUsers())) {
//            throw new RuntimeException("适应人群为空");
            MomoClassException.cast("适应人群为空");
        }
        if (StringUtils.isBlank(addCourseDto.getCharge())) {
//            throw new RuntimeException("收费规则为空");
            MomoClassException.cast("收费规则为空");
        }

        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDto, courseBase);
        courseBase.setAuditStatus("202002");
        courseBase.setStatus("203001");
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        int insert = courseBaseMapper.insert(courseBase);
        if (insert <= 0) {
//            throw new RuntimeException("新增课程信息失败");
            MomoClassException.cast("新增课程信息失败");
        }

        CourseMarket courseMarket = new CourseMarket();
        Long courseId = courseBase.getId();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        courseMarket.setId(courseId);
        int result = saveCourseMarket(courseMarket);
        if (result <= 0) {
//            throw new RuntimeException("保存课程营销信息失败");
            MomoClassException.cast("保存课程营销信息失败");
        }
        return getCourseBaseInfo(courseId);
    }

    // 根据id查询课程信息
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        String mt = courseBase.getMt();
        CourseCategory courseCategoryMt = courseCategoryMapper.selectById(mt);
        courseBaseInfoDto.setMtName(courseCategoryMt.getName());
        String st = courseBase.getSt();
        CourseCategory courseCategorySt = courseCategoryMapper.selectById(st);
        courseBaseInfoDto.setStName(courseCategorySt.getName());
        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        Long courseId = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            MomoClassException.cast("课程不存在");
        }
        // 数据合法性校验
        if (!companyId.equals(courseBase.getCompanyId())) {
            MomoClassException.cast("本机构只能修改本机构的课程");
        }

        BeanUtils.copyProperties(editCourseDto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        int updateBase = courseBaseMapper.updateById(courseBase);
        if (updateBase <= 0) {
            MomoClassException.cast("修改课程失败");
        }

        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        int updateMarket = courseMarketMapper.updateById(courseMarket);
        if (updateMarket <= 0) {
            MomoClassException.cast("修改课程营销信息失败");
        }

        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseInfo(courseId);
        return courseBaseInfoDto;
    }

    @Override
    public void deleteCourse(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (!companyId.equals(courseBase.getCompanyId())) {
            MomoClassException.cast("只允许删除本机构的课程");
        } else {
            // 删除教师信息
            LambdaQueryWrapper<CourseTeacher> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(CourseTeacher::getCourseId, courseId);
            courseTeacherMapper.delete(teacherWrapper);
            // 删除课程计划
            LambdaQueryWrapper<Teachplan> teachplanWrapper = new LambdaQueryWrapper<>();
            teachplanWrapper.eq(Teachplan::getCourseId, courseId);
            teachplanMapper.delete(teachplanWrapper);
            // 删除营销信息
            courseMarketMapper.deleteById(courseId);
            // 删除基本信息
            courseBaseMapper.deleteById(courseId);
        }

    }

    // 保存营销信息, 存在则更新, 不存在则保存
    private int saveCourseMarket(CourseMarket courseMarket) {
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)) {
//            throw new RuntimeException("收费规则为空");
            MomoClassException.cast("收费规则为空");
        }
        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null) {
//                throw new RuntimeException("课程的价格不能为空");
                MomoClassException.cast("课程的价格不能为空");
            } else if (courseMarket.getPrice().floatValue() <= 0) {
//                throw new RuntimeException("课程的价格不能小于0");
                MomoClassException.cast("课程的价格不能小于0");
            }
        }

        Long id = courseMarket.getId();
        CourseMarket courseMarketGetFromData = courseMarketMapper.selectById(id);
        if (courseMarketGetFromData == null) {
            // 不存在，插入新数据
            int insert = courseMarketMapper.insert(courseMarket);
            return insert;
        } else {
            // 存在，更新数据
            BeanUtils.copyProperties(courseMarket, courseMarketGetFromData);
            courseMarketGetFromData.setId(courseMarket.getId());

            int update = courseMarketMapper.updateById(courseMarketGetFromData);
            return update;
        }
    }
}
