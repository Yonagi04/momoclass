package com.momoclass.base.exception;

/**
 * @author Yonagi
 * @version 1.0
 * @date 2024/3/11
 * @description 通用异常类
 */
public class MomoClassException extends RuntimeException{
    private String errMessage;

    public MomoClassException() {
    }

    public MomoClassException(String message) {
        super(message);
        this.errMessage = message;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public static void cast(String message) {
        throw new MomoClassException(message);
    }
    public static void cast(CommonError error) {
        throw new MomoClassException(error.getErrMessage());
    }
}
