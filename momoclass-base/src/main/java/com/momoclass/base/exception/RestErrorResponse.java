package com.momoclass.base.exception;

import java.io.Serializable;

/**
 * @author Yonagi
 * @version 1.0
 * @description 异常返回
 * @date 2024/3/11
 */
public class RestErrorResponse implements Serializable {
    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
