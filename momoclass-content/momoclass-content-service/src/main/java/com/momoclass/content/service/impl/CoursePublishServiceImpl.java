package com.momoclass.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.momoclass.base.exception.MomoClassException;
import com.momoclass.content.mapper.CourseBaseMapper;
import com.momoclass.content.mapper.CourseMarketMapper;
import com.momoclass.content.mapper.CoursePublishPreMapper;
import com.momoclass.content.model.dto.CourseBaseInfoDto;
import com.momoclass.content.model.dto.CoursePreviewDto;
import com.momoclass.content.model.dto.TeachplanDto;
import com.momoclass.content.model.po.CourseBase;
import com.momoclass.content.model.po.CourseMarket;
import com.momoclass.content.model.po.CoursePublish;
import com.momoclass.content.model.po.CoursePublishPre;
import com.momoclass.content.service.CourseBaseInfoService;
import com.momoclass.content.service.CoursePublishService;
import com.momoclass.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 课程发布
 * @date 2024/03/22 15:03
 */
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfo);
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }

    @Override
    public void commitAudit(Long courseId, Long companyId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        String auditStatus = courseBase.getAuditStatus();
        if (auditStatus.equals("202003")) {
            MomoClassException.cast("当前为待审核状态");
        }
        if (!courseBase.getCompanyId().equals(companyId)) {
            MomoClassException.cast("不允许提交其他机构的数据");
        }
        if (StringUtils.isEmpty(courseBase.getPic())) {
            MomoClassException.cast("请上传课程图片");
        }
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String courseMarketJSON = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(courseMarketJSON);
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (teachplanTree == null || teachplanTree.size() <= 0) {
            MomoClassException.cast("还没有添加课程计划");
        }
        String teachplanTreeJSON = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplanTreeJSON);

        coursePublishPre.setStatus("202003");
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreUpdate == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }
}
