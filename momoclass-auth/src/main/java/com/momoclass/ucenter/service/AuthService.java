package com.momoclass.ucenter.service;

import com.momoclass.ucenter.model.dto.AuthParamsDto;
import com.momoclass.ucenter.model.dto.XcUserExt;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 统一认证接口
 * @date 2024/03/25 18:42
 */
public interface AuthService {
    XcUserExt execute(AuthParamsDto authParamsDto);
}
