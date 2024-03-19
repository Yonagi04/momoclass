package com.momoclass.media.service;

import com.momoclass.media.model.po.MediaProcess;

import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 媒体文件处理业务方法
 * @date 2024/03/19 19:01
 */
public interface MediaFileProcessService {
    // 获取待处理任务
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);
    // 开启一个任务
    public boolean startTask(long id);
    // 保存任务结果
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
