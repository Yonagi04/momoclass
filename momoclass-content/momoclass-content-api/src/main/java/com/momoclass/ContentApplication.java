package com.momoclass;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Yonagi
 * @date 2024/3/8
 * @version 1.0
 * @description 内容管理服务启动类
 */
@EnableFeignClients(basePackages = {"com.momoclass.content.feignclient"})
@EnableSwagger2Doc
@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
