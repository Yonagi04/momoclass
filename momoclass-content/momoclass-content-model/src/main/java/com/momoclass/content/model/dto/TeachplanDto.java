package com.momoclass.content.model.dto;

import com.momoclass.content.model.po.Teachplan;
import com.momoclass.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 课程计划信息模型类
 * @date 2024/03/11 19:05
 */
public class TeachplanDto extends Teachplan {
    // 小章节list
    private List<TeachplanDto> teachPlanTreeNodes;

    // 与媒资管理的信息
    private TeachplanMedia teachplanMedia;

    public List<TeachplanDto> getTeachPlanTreeNodes() {
        return teachPlanTreeNodes;
    }

    public void setTeachPlanTreeNodes(List<TeachplanDto> teachPlanTreeNodes) {
        this.teachPlanTreeNodes = teachPlanTreeNodes;
    }

    public TeachplanMedia getTeachplanMedia() {
        return teachplanMedia;
    }

    public void setTeachplanMedia(TeachplanMedia teachplanMedia) {
        this.teachplanMedia = teachplanMedia;
    }
}
