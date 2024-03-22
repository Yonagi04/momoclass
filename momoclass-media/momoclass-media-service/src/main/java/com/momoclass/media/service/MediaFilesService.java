package com.momoclass.media.service;

import com.momoclass.base.model.PageParams;
import com.momoclass.base.model.PageResult;
import com.momoclass.base.model.RestResponse;
import com.momoclass.media.model.dto.QueryMediaParamsDto;
import com.momoclass.media.model.dto.UploadFileParamsDto;
import com.momoclass.media.model.dto.UploadFileResultDto;
import com.momoclass.media.model.po.MediaFiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 媒体文件管理业务类
 * @date 2024/03/14 11:23
 */
public interface MediaFilesService {
    // 媒体文件查询
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);
    // 上传媒体文件
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);
    // 添加文件记录到数据库
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName);
    // 检查文件是否存在
    public RestResponse<Boolean> checkFile(String fileMd5);
    // 检查文件分块是否存在
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);
    // 上传分块
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath);
    // 合并分块
    public RestResponse mergeChunk(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    public File downloadFileFromMinIO(String bucket, String objectName);
    public boolean addMediaFilesToMinIO(String localFilePath,String
            mimeType,String bucket, String objectName);

    public MediaFiles getFileById(String mediaId);
}
