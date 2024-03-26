package com.momoclass.ucenter.service;

import com.momoclass.ucenter.model.po.XcUser;

public interface WxAuthService {
    XcUser wxAuth(String code);
}
