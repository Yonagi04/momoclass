package com.momoclass.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.momoclass.base.exception.MomoClassException;
import com.momoclass.content.mapper.TeachplanMapper;
import com.momoclass.content.model.dto.SaveTeachplanDto;
import com.momoclass.content.model.dto.TeachplanDto;
import com.momoclass.content.model.po.Teachplan;
import com.momoclass.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 课程计划管理服务层实现
 * @date 2024/03/11 20:09
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Long teachplanId = saveTeachplanDto.getId();
        if (teachplanId == null) {
            // 新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);

            Long parentid = saveTeachplanDto.getParentid();
            Long courseId = saveTeachplanDto.getCourseId();
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
            Integer count = teachplanMapper.selectCount(queryWrapper);
            teachplan.setOrderby(count + 1);

            int insert = teachplanMapper.insert(teachplan);
            if (insert <= 0) {
                MomoClassException.cast("新增课程计划失败");
            }
        } else {
            // 修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            int update = teachplanMapper.updateById(teachplan);
            if (update <= 0) {
                MomoClassException.cast("修改课程计划失败");
            }
        }
    }

    @Override
    public void deleteTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);

        Long parentid = teachplan.getParentid();
        Long courseId = teachplan.getCourseId();
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, id);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        if (count != 0) {
            MomoClassException.cast("课程计划信息还有子级信息，无法操作");
        } else {
            teachplanMapper.deleteById(id);
        }
    }
}
