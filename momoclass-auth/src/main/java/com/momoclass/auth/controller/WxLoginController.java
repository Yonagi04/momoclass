package com.momoclass.auth.controller;

import com.momoclass.ucenter.model.dto.XcUserExt;
import com.momoclass.ucenter.model.po.XcUser;
import com.momoclass.ucenter.service.impl.WxAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description
 * @date 2024/03/25 20:38
 */
@Slf4j
@Controller
public class WxLoginController {
    @Autowired
    WxAuthServiceImpl wxAuthService;

    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) throws Exception {
        log.debug("微信扫码回调,code:{},state:{}",code,state);
        //请求微信申请令牌，拿到令牌查询用户信息，将用户信息写入本项目数据库
        XcUser xcUser = wxAuthService.wxAuth(code);
        if(xcUser==null){
            return "redirect:http://www.51xuecheng.cn/error.html";
        }
        String username = xcUser.getUsername();
        return
                "redirect:http://www.51xuecheng.cn/sign.html?username="+username+" &authType=wx";
    }
}
