package com.momoclass.content.service.jobhandler;

import com.momoclass.base.exception.MomoClassException;
import com.momoclass.content.feignclient.SearchServiceClient;
import com.momoclass.content.mapper.CoursePublishMapper;
import com.momoclass.content.model.dto.CourseIndex;
import com.momoclass.content.model.dto.CoursePreviewDto;
import com.momoclass.content.model.po.CoursePublish;
import com.momoclass.content.service.CoursePublishService;
import com.momoclass.messagesdk.model.po.MqMessage;
import com.momoclass.messagesdk.service.MessageProcessAbstract;
import com.momoclass.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 课程发布任务类
 * @date 2024/03/23 13:09
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {
    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    SearchServiceClient searchServiceClient;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        process(shardIndex, shardTotal, "course_publish", 30, 60);

    }

    @Override
    public boolean execute(MqMessage mqMessage) {
        // mqMessage拿到课程id
        Long courseId = Long.parseLong(mqMessage.getBusinessKey1());

        // 向minio写静态化课程
        generateCourseHtml(mqMessage, courseId);
        // 向es写索引
        saveCourseIndex(mqMessage, courseId);
        // 向redis写缓存

        // 全部完成返回true
        return true;
    }

    private void generateCourseHtml(MqMessage mqMessage, long courseId) {
        // 做任务幂等处理
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne > 0) {
            log.debug("课程静态化已经完成，无需处理");
            return;
        }
        // 开始课程静态化
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file != null) {
            coursePublishService.uploadCourseHtml(courseId, file);
        } else {
            MomoClassException.cast("生成的静态页面为空");
        }
        // 任务处理完成写任务状态为完成
        mqMessageService.completedStageOne(taskId);
    }

    private void saveCourseIndex(MqMessage mqMessage, long courseId) {
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        int stageTwo = mqMessageService.getStageTwo(taskId);
        if (stageTwo > 0) {
            log.debug("课程索引写入已经完成，无需处理");
            return;
        }
        // 开始写入课程索引
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);
        Boolean add = searchServiceClient.add(courseIndex);
        if (!add) {
            MomoClassException.cast("索引添加失败");
        }
        // 任务处理完成写任务状态为完成
        mqMessageService.completedStageTwo(taskId);
    }
}
