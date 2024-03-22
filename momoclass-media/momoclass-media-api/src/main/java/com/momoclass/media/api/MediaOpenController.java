package com.momoclass.media.api;

import com.momoclass.base.exception.MomoClassException;
import com.momoclass.base.model.RestResponse;
import com.momoclass.media.model.po.MediaFiles;
import com.momoclass.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description
 * @date 2024/03/22 16:10
 */
@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {
    @Autowired
    MediaFilesService mediaFilesService;

    @ApiOperation(value = "预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId) {
        MediaFiles mediaFiles = mediaFilesService.getFileById(mediaId);
        if (mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())) {
            MomoClassException.cast("视频没有做转码处理");
        }
        return RestResponse.success(mediaFiles.getUrl());
    }
}
