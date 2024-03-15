package com.momoclass.media.api;

import com.momoclass.base.exception.MomoClassException;
import com.momoclass.base.model.PageParams;
import com.momoclass.base.model.PageResult;
import com.momoclass.media.model.dto.QueryMediaParamsDto;
import com.momoclass.media.model.dto.UploadFileParamsDto;
import com.momoclass.media.model.dto.UploadFileResultDto;
import com.momoclass.media.model.po.MediaFiles;
import com.momoclass.media.service.MediaFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 媒体文件管理接口
 * @date 2024/03/14 10:55
 */

@Api(value = "媒体文件管理接口", tags = "媒体文件管理接口")
@RestController
public class MediaFilesController {
    @Autowired
    MediaFilesService mediaFileService;

    @ApiOperation("媒体列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);
    }

    @ApiOperation("上传文件")
    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public UploadFileResultDto uploadFile(@RequestPart("filedata") MultipartFile upload,
                                          @RequestParam(value = "folder", required = false) String folder,
                                          @RequestParam(value = "objectName", required = false) String objectName) throws IOException {
        // 新上传文件
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        // 文件大小
        uploadFileParamsDto.setFileSize(upload.getSize());
        if (upload.getContentType().contains("image")) {
            // 文件类型
            uploadFileParamsDto.setFileType("001001");
        } else {
            uploadFileParamsDto.setFileType("001003");
        }

        // 文件名
        uploadFileParamsDto.setFileName(upload.getOriginalFilename());
        // 文件content-type
        uploadFileParamsDto.setContentType(upload.getContentType());

        Long companyId = 1232141425L;

        try {
            UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, upload.getBytes(), folder, objectName);
            return uploadFileResultDto;
        } catch (IOException e) {
            MomoClassException.cast("上传文件过程出错");
        }
        return null;
    }
}
