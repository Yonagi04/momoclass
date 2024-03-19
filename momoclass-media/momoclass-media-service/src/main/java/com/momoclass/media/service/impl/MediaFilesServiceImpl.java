package com.momoclass.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.momoclass.base.exception.MomoClassException;
import com.momoclass.base.model.PageParams;
import com.momoclass.base.model.PageResult;
import com.momoclass.base.model.RestResponse;
import com.momoclass.media.mapper.MediaFilesMapper;
import com.momoclass.media.mapper.MediaProcessMapper;
import com.momoclass.media.model.dto.QueryMediaParamsDto;
import com.momoclass.media.model.dto.UploadFileParamsDto;
import com.momoclass.media.model.dto.UploadFileResultDto;
import com.momoclass.media.model.po.MediaFiles;
import com.momoclass.media.model.po.MediaProcess;
import com.momoclass.media.service.MediaFilesService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 媒体文件管理业务实现
 * @date 2024/03/14 11:25
 */
@Slf4j
@Service
public class MediaFilesServiceImpl implements MediaFilesService {
    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MediaProcessMapper mediaProcessMapper;

    @Autowired
    MinioClient minioClient;

    @Autowired
    MediaFilesService currentProxy;

    @Value("${minio.bucket.files}")
    private String bucketFiles;

    @Value("${minio.bucket.videofiles}")
    private String bucketVideo;

    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {
        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;
    }

    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/")+"/";
        return folder;
    }

    //获取文件的 md5
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new
                FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getMimeType(String extension){
        if(extension==null)
            extension = "";
        //根据扩展名取出 mimeType
        ContentInfo extensionMatch =
                ContentInfoUtil.findExtensionMatch(extension);
        //通用 mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    @Override
    public boolean addMediaFilesToMinIO(String localFilePath,String
            mimeType,String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传到minio成功，bucket:{}, objectName:{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到 minio 出错,bucket:{},objectName:{},错误原因:{}",bucket,objectName,e.getMessage(),e);
            MomoClassException.cast("上传文件到文件系统失败");
        }
        return false;
    }

    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId,String
            fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String
                                                objectName){
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
                MomoClassException.cast("保存文件信息失败");
            }
            addWaitingTask(mediaFiles);
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());
        }
        return mediaFiles;
    }

    // 添加待处理任务
    private void addWaitingTask(MediaFiles mediaFiles) {
        String filename = mediaFiles.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        String mimeType = getMimeType(extension);
        if (mimeType.equals("video/x-msvideo")) {
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles, mediaProcess);
            mediaProcess.setStatus("1");
            mediaProcess.setFailCount(0);
            mediaProcessMapper.insert(mediaProcess);
        }
    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        File file = new File(localFilePath);
        if (!file.exists()) {
            MomoClassException.cast("文件不存在");
        }
        //文件名称
        String filename = uploadFileParamsDto.getFileName();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件 mimeType
        String mimeType = getMimeType(extension);
        //文件的 md5 值
        String fileMd5 = getFileMd5(file);
        //文件的默认目录
        String defaultFolderPath = getDefaultFolderPath();
        //存储到 minio 中的对象名(带目录)
        String objectName = defaultFolderPath + fileMd5 + extension;
        //将文件上传到 minio
        boolean b = addMediaFilesToMinIO(localFilePath, mimeType,
                bucketFiles, objectName);
        //文件大小
        uploadFileParamsDto.setFileSize(file.length());

        //将文件信息存储到数据库
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5,
                uploadFileParamsDto, bucketFiles, objectName);
        //准备返回数据
        UploadFileResultDto uploadFileResultDto = new
                UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            String bucket = mediaFiles.getBucket();
            String filePath = mediaFiles.getFilePath();
            try {
                InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(filePath)
                        .build());
                if (stream != null) {
                    return RestResponse.success(true);
                }
            } catch (Exception e) {

            }
        }
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        try {
            InputStream fileInputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketVideo)
                    .object(chunkFilePath)
                    .build());
            if (fileInputStream != null) {
                return RestResponse.success(true);
            }
        } catch (Exception e) {

        }
        return RestResponse.success(false);
    }

    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        String chunkFilePath = chunkFileFolderPath + chunk;
        String mimeType = getMimeType(null);
        boolean b = addMediaFilesToMinIO(localChunkFilePath, mimeType, bucketVideo, chunkFilePath);
        if (!b) {
            log.debug("上传分块文件失败:{}", chunkFilePath);
            return RestResponse.validfail(false, "上传分块失败");
        }
        log.debug("上传分块文件成功:{}", chunkFilePath);
        return RestResponse.success(true);
    }

    @Override
    public RestResponse mergeChunk(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        // 获取分块文件路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        // 将分块文件路径组成List<ComposeSource>
        List<ComposeSource> sourcesObjectList = Stream.iterate(0, i -> i++)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucketVideo)
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        // 合并
        String fileName = uploadFileParamsDto.getFileName();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String mergeFilePath = getFilePathByMd5(fileMd5, extension);
        try {
            ObjectWriteResponse response = minioClient.composeObject(ComposeObjectArgs.builder()
                    .bucket(bucketVideo)
                    .object(mergeFilePath)
                    .sources(sourcesObjectList)
                    .build());
            log.debug("合并分块成功:{}", mergeFilePath);
        } catch (Exception e) {
            log.debug("合并文件失败, 文件md5{}, 异常{}", fileMd5, e, e.getMessage());
            return RestResponse.validfail(false, "合并文件失败");
        }
        // 验证md5
        File minioFile = downloadFileFromMinIO(bucketVideo, mergeFilePath);
        if (minioFile == null) {
            log.debug("下载合并后文件失败, mergeFilePath:{}", mergeFilePath);
            return RestResponse.validfail(false, "下载合并文件失败");
        }
        try (InputStream newFileInputStream = new FileInputStream(minioFile)) {
            String md5Hex = DigestUtils.md5Hex(newFileInputStream);
            if (!fileMd5.equals(md5Hex)) {
                return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
            }
            uploadFileParamsDto.setFileSize(minioFile.length());
        } catch (Exception e) {
            log.debug("校验文件失败, fileMd5:{}, 异常:{}", fileMd5, e, e.getMessage());
            return RestResponse.validfail(false, "文件校验失败");
        } finally {
            if (minioFile != null) {
                minioFile.delete();
            }
        }
        // 文件入库
        currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucketVideo, mergeFilePath);
        clearChunkFiles(chunkFileFolderPath, chunkTotal);
        return RestResponse.success(true);
    }

    @Override
    public File downloadFileFromMinIO(String bucket, String objectName) {
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            minioFile = File.createTempFile("minio", "merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(outputStream!=null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String getFilePathByMd5(String fileMd5, String extension) {
        return fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 + extension;
    }

    private void clearChunkFiles(String chunkFileFolderPath,int chunkTotal) {
        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> i++)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());
            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                    .bucket("video")
                    .objects(deleteObjects)
                    .build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r -> {
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清除分块文件失败, objectName:{}", deleteError.objectName(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清除分块文件失败, chunkFileFolderPath:{}", chunkFileFolderPath, e);
        }
    }
}
