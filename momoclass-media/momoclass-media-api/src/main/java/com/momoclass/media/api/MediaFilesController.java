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

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiles(companyId,pageParams,queryMediaParamsDto);
    }

    @ApiOperation("上传文件")
    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public UploadFileResultDto uploadFile(@RequestPart("filedata") MultipartFile upload,
                                          @RequestParam(value = "folder", required = false) String folder,
                                          @RequestParam(value = "objectName", required = false) String objectName) throws IOException {
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileSize(upload.getSize());
        uploadFileParamsDto.setFileType("001001");
        uploadFileParamsDto.setFileName(upload.getOriginalFilename());
        // 创建临时文件
        File tempFile = File.createTempFile("minio", "temp");
        // 上传的文件拷贝到临时文件
        upload.transferTo(tempFile);
        // 文件路径
        String absolutePath = tempFile.getAbsolutePath();
        // 上传文件
        UploadFileResultDto uploadFileResultDto =
                mediaFileService.uploadFile(companyId, uploadFileParamsDto,
                        absolutePath, objectName);

        return uploadFileResultDto;
    }
}
