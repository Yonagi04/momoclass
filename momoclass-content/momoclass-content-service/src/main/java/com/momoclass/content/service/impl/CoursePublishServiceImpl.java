package com.momoclass.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.momoclass.base.exception.CommonError;
import com.momoclass.base.exception.MomoClassException;
import com.momoclass.content.config.MultipartSupportConfig;
import com.momoclass.content.feignclient.MediaServiceClient;
import com.momoclass.content.mapper.CourseBaseMapper;
import com.momoclass.content.mapper.CourseMarketMapper;
import com.momoclass.content.mapper.CoursePublishMapper;
import com.momoclass.content.mapper.CoursePublishPreMapper;
import com.momoclass.content.model.dto.CourseBaseInfoDto;
import com.momoclass.content.model.dto.CoursePreviewDto;
import com.momoclass.content.model.dto.TeachplanDto;
import com.momoclass.content.model.po.CourseBase;
import com.momoclass.content.model.po.CourseMarket;
import com.momoclass.content.model.po.CoursePublish;
import com.momoclass.content.model.po.CoursePublishPre;
import com.momoclass.content.service.CourseBaseInfoService;
import com.momoclass.content.service.CoursePublishService;
import com.momoclass.content.service.TeachplanService;
import com.momoclass.messagesdk.model.po.MqMessage;
import com.momoclass.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 课程发布
 * @date 2024/03/22 15:03
 */
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfo);
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }

    @Override
    public void commitAudit(Long courseId, Long companyId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        String auditStatus = courseBase.getAuditStatus();
        if (auditStatus.equals("202003")) {
            MomoClassException.cast("当前为待审核状态");
        }
        if (!courseBase.getCompanyId().equals(companyId)) {
            MomoClassException.cast("不允许提交其他机构的数据");
        }
        if (StringUtils.isEmpty(courseBase.getPic())) {
            MomoClassException.cast("请上传课程图片");
        }
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String courseMarketJSON = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(courseMarketJSON);
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (teachplanTree == null || teachplanTree.size() <= 0) {
            MomoClassException.cast("还没有添加课程计划");
        }
        String teachplanTreeJSON = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplanTreeJSON);

        coursePublishPre.setStatus("202003");
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreUpdate == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    @Transactional
    @Override
    public void coursePublish(Long companyId, Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            MomoClassException.cast("请先提交课程审核，审核完成了才能发布");
        }

        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            MomoClassException.cast("只能发布本机构的课程");
        }

        String auditStatus = coursePublishPre.getStatus();
        if (!auditStatus.equals("202004")) {
            MomoClassException.cast("只有审核完成了才能发布课程");
        }
        saveCoursePublish(courseId);
        saveCoursePublishMessage(courseId);
        coursePublishPreMapper.deleteById(courseId);
    }

    private void saveCoursePublish(Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            MomoClassException.cast("请先提交课程审核，审核完成了才能发布");
        }

        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus("202004");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if (coursePublishUpdate == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }

    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course-publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            MomoClassException.cast(CommonError.UNKNOW_ERROR);
        }
    }

    @Override
    public File generateCourseHtml(Long courseId) {
        //静态化文件
        File htmlFile = null;
        try {
            //配置 freemarker
            Configuration configuration = new
                    Configuration(Configuration.getVersion());
            //加载模板
            //选指定模板路径,classpath 下 templates 下
            //得到 classpath 路径
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");
            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");
            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);
            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);
            //静态化
            //参数 1：模板，参数 2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            // System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //创建静态化文件
            htmlFile = File.createTempFile("course",".html");
            log.debug("课程静态化，生成静态文件:{}",htmlFile.getAbsolutePath());
            //输出流
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("课程静态化异常:{}",e.toString());
            MomoClassException.cast("课程静态化异常");
        }
        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String course = mediaServiceClient.uploadFile(multipartFile, "course/"+courseId+".html");
        if (course == null){
            MomoClassException.cast("上传静态文件异常");
        }
    }
}
