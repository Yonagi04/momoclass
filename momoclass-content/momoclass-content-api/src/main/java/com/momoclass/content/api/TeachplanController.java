package com.momoclass.content.api;

import com.momoclass.content.model.dto.BindTeachplanMediaDto;
import com.momoclass.content.model.dto.SaveTeachplanDto;
import com.momoclass.content.model.dto.TeachplanDto;
import com.momoclass.content.model.po.Teachplan;
import com.momoclass.content.model.po.TeachplanMedia;
import com.momoclass.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 课程计划管理相关接口
 * @date 2024/03/11 19:07
 */
@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
@RestController
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody TeachplanDto teachplanDto) {
        teachplanService.saveTeachplan(teachplanDto);
    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable Long id) {
        teachplanService.deleteTeachplan(id);
    }

    @ApiOperation("课程计划移动")
    @PostMapping("/teachplan/{moveType}/{id}")
    public void moveTeachplan(@PathVariable String moveType, @PathVariable Long id) {
        teachplanService.moveTeachplan(moveType, id);
    }

    @ApiOperation("课程计划和媒体文件绑定")
    @PostMapping("/teachplan/association/media")
    public TeachplanMedia associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto) {
        return teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation("课程计划和媒体文件解绑定")
    @DeleteMapping("/teachplan/association/media/{teachplanId}/{mediaId}")
    public void unassociationMedia(@PathVariable Long teachplanId, @PathVariable Long mediaId) {
        teachplanService.unassociationMedia(teachplanId, mediaId);
    }
}
