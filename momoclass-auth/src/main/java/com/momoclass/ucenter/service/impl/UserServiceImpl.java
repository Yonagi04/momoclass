package com.momoclass.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.momoclass.ucenter.mapper.XcUserMapper;
import com.momoclass.ucenter.model.dto.AuthParamsDto;
import com.momoclass.ucenter.model.dto.XcUserExt;
import com.momoclass.ucenter.model.po.XcUser;
import com.momoclass.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description
 * @date 2024/03/25 15:53
 */
@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, s));
//        if (xcUser == null) {
//            return null;
//        }
//
//        String password = xcUser.getPassword();
//        String[] authorities= {"p1"};
//        xcUser.setPassword(null);
//        String userString = JSON.toJSONString(xcUser);
//
//        UserDetails userDetails = User.withUsername(userString)
//                .password(password)
//                .authorities(authorities)
//                .build();
//
//        return userDetails;
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.error("认证请求不符合项目要求:{}", s);
            throw new RuntimeException("认证请求数据有问题");
        }
        // 认证类型
        String authType = authParamsDto.getAuthType();
        // 根据认证类型获得bean
        String beanName = authType + "_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        // 调用
        XcUserExt user = authService.execute(authParamsDto);

        return getUserPrincipal(user);
    }

    public UserDetails getUserPrincipal(XcUserExt user) {
        String[] authorities = {"p1"};
        String password = user.getPassword();
        user.setPassword(null);
        String userString = JSON.toJSONString(user);
        UserDetails userDetails = User.withUsername(userString)
                .password(password)
                .authorities(authorities)
                .build();
        return userDetails;
    }
}
