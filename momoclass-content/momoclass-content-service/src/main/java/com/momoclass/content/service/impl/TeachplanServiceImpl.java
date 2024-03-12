package com.momoclass.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.momoclass.base.exception.MomoClassException;
import com.momoclass.content.mapper.TeachplanMapper;
import com.momoclass.content.mapper.TeachplanMediaMapper;
import com.momoclass.content.model.dto.SaveTeachplanDto;
import com.momoclass.content.model.dto.TeachplanDto;
import com.momoclass.content.model.po.Teachplan;
import com.momoclass.content.model.po.TeachplanMedia;
import com.momoclass.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachplan(TeachplanDto teachplanDto) {
        Long teachplanId = teachplanDto.getId();
        if (teachplanId == null) {
            Teachplan plan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, plan);
            plan.setCreateDate(LocalDateTime.now());
            plan.setOrderby(getTeachPlanCount(plan.getCourseId(), plan.getParentid()) + 1);
            int insert = teachplanMapper.insert(plan);
            if (insert <= 0) {
                MomoClassException.cast("新增失败");
            }
        } else {
            Teachplan plan = teachplanMapper.selectById(teachplanId);
            BeanUtils.copyProperties(teachplanDto, plan);
            plan.setChangeDate(LocalDateTime.now());
            int update = teachplanMapper.updateById(plan);
            if (update <= 0) {
                MomoClassException.cast("修改失败");
            }
        }
    }

    private Integer getTeachPlanCount(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId)
                .eq(Teachplan::getParentid, parentid);
        return teachplanMapper.selectCount(queryWrapper);
    }

//    @Transactional
//    @Override
//    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
//        Long teachplanId = saveTeachplanDto.getId();
//        if (teachplanId == null) {
//            // 新增
//            Teachplan teachplan = new Teachplan();
//            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
//
//            Long parentid = saveTeachplanDto.getParentid();
//            Long courseId = saveTeachplanDto.getCourseId();
//            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper = queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
//            Integer count = teachplanMapper.selectCount(queryWrapper);
//            teachplan.setOrderby(count + 1);
//
//            int insert = teachplanMapper.insert(teachplan);
//            if (insert <= 0) {
//                MomoClassException.cast("新增课程计划失败");
//            }
//        } else {
//            // 修改
//            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
//            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
//            int update = teachplanMapper.updateById(teachplan);
//            if (update <= 0) {
//                MomoClassException.cast("修改课程计划失败");
//            }
//        }
//    }

    @Override
    public void deleteTeachplan(Long id) {
        if (id == null) {
            MomoClassException.cast("课程计划id为空");
        }
        Teachplan teachplan = teachplanMapper.selectById(id);

        Long courseId = teachplan.getCourseId();
        Integer grade = teachplan.getGrade();

        if (grade == 1) {
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, id);
            Integer count = teachplanMapper.selectCount(queryWrapper);
            if (count > 0) {
                MomoClassException.cast("课程计划下还有子级信息，无法操作");
            }
            teachplanMapper.deleteById(id);
        } else {
            teachplanMapper.deleteById(id);
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId, id);
            teachplanMediaMapper.delete(queryWrapper);
        }
    }


    @Transactional
    @Override
    public void moveTeachplan(String moveType, Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);

        Integer grade = teachplan.getGrade();
        Integer orderby = teachplan.getOrderby();
        // 大章节移动
        Long courseId = teachplan.getCourseId();
        // 小章节移动
        Long parentid = teachplan.getParentid();
        if ("moveup".equals(moveType)) {
            if (grade == 1) {
                // 章节上移，找到上一个章节的orderby，然后与其交换orderby
                // SELECT * FROM teachplan WHERE courseId = 117 AND grade = 1  AND orderby < 1 ORDER BY orderby DESC limit 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getGrade, 1)
                        .eq(Teachplan::getCourseId, courseId)
                        .lt(Teachplan::getOrderby, orderby)
                        .orderByDesc(Teachplan::getOrderby)
                        .last("limit 1");
                Teachplan search = teachplanMapper.selectOne(queryWrapper);
                swapOrderBy(teachplan, search);
            } else if (grade == 2) {
                // 小节上移
                // SELECT * FROM teachplan WHERE parentId = 268 AND orderby < 5 ORDER BY orderby DESC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getParentid, parentid)
                        .lt(Teachplan::getOrderby, orderby)
                        .orderByDesc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan search = teachplanMapper.selectOne(queryWrapper);
                swapOrderBy(teachplan, search);
            }

        } else if ("movedown".equals(moveType)) {
            if (grade == 1) {
                // 章节下移
                // SELECT * FROM teachplan WHERE courseId = 117 AND grade = 1 AND orderby > 1 ORDER BY orderby ASC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getCourseId, courseId)
                        .eq(Teachplan::getGrade, grade)
                        .gt(Teachplan::getOrderby, orderby)
                        .orderByAsc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan search = teachplanMapper.selectOne(queryWrapper);
                swapOrderBy(teachplan, search);
            } else if (grade == 2) {
                // 小节下移
                // SELECT * FROM teachplan WHERE parentId = 268 AND orderby > 1 ORDER BY orderby ASC LIMIT 1
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getParentid, parentid)
                        .gt(Teachplan::getOrderby, orderby)
                        .orderByAsc(Teachplan::getOrderby)
                        .last("LIMIT 1");
                Teachplan search = teachplanMapper.selectOne(queryWrapper);
                swapOrderBy(teachplan, search);
            }
        }
    }

    // 交换两个teachplan的orderby
    private void swapOrderBy(Teachplan teachplan, Teachplan search) {
        if (search == null)
            MomoClassException.cast("已经到头啦，不能再移啦");
        else {
            // 交换orderby，更新
            Integer orderby = teachplan.getOrderby();
            Integer tmpOrderby = search.getOrderby();
            teachplan.setOrderby(tmpOrderby);
            search.setOrderby(orderby);
            teachplanMapper.updateById(search);
            teachplanMapper.updateById(teachplan);
        }
    }
}
