package com.momoclass.ucenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description
 * @date 2024/03/25 20:03
 */
@FeignClient(value = "checkcode",fallbackFactory = CheckCodeClientFactory.class)
@RequestMapping("/checkcode")
public interface CheckCodeClient {
    @PostMapping(value = "/verify")
    public Boolean verify(@RequestParam("key") String key, @RequestParam("code") String code);
}
