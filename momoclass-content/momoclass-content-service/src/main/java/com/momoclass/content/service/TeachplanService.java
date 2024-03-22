package com.momoclass.content.service;

import com.momoclass.content.model.dto.BindTeachplanMediaDto;
import com.momoclass.content.model.dto.SaveTeachplanDto;
import com.momoclass.content.model.dto.TeachplanDto;
import com.momoclass.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 课程计划管理服务层
 * @date 2024/03/11 19:10
 */
public interface TeachplanService {
    // 课程计划查询
    public List<TeachplanDto> findTeachplanTree(Long courseId);

    // 保存/修改课程计划
    // public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);
    public void saveTeachplan(TeachplanDto teachplanDto);

    // 删除课程计划
    public void deleteTeachplan(Long id);


    // 移动课程计划
    public void moveTeachplan(String moveType, Long id);

    // 课程计划和媒体文件绑定
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    // 课程计划和媒体文件解绑定
    public void unassociationMedia(Long teachplanId, Long mediaId);
}
