package com.momoclass.content.service.jobhandler;

import com.momoclass.messagesdk.model.po.MqMessage;
import com.momoclass.messagesdk.service.MessageProcessAbstract;
import com.momoclass.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

        // 任务处理完成写任务状态为完成
        mqMessageService.completedStageTwo(taskId);
    }
}
