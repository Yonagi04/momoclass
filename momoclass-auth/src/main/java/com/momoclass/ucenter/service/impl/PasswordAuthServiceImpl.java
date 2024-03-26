package com.momoclass.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.momoclass.ucenter.feignclient.CheckCodeClient;
import com.momoclass.ucenter.mapper.XcUserMapper;
import com.momoclass.ucenter.model.dto.AuthParamsDto;
import com.momoclass.ucenter.model.dto.XcUserExt;
import com.momoclass.ucenter.model.po.XcUser;
import com.momoclass.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 账号密码登录
 * @date 2024/03/25 18:43
 */
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        // 账号
        String username = authParamsDto.getUsername();

        // 远程调用验证码服务验证码校验
        String checkcode = authParamsDto.getCheckcode();
        // 验证码对应的key
        String checkcodekey = authParamsDto.getCheckcodekey();
        if (StringUtils.isEmpty(checkcode) || StringUtils.isEmpty(checkcodekey)) {
            throw new RuntimeException("验证码为空");
        }
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (verify == null || !verify) {
            throw new RuntimeException("验证码输入错误");
        }
        // 账号是否存在, 如果不存在就抛异常
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }
        //校验密码
        String passwordDb = user.getPassword();
        String passwordParam = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordParam, passwordDb);
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user, xcUserExt);

        return xcUserExt;
    }
}
