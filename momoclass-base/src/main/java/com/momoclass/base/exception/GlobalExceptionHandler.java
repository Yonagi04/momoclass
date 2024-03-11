package com.momoclass.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yonagi
 * @version 1.0
 * @program momoclass-project
 * @description 异常处理类
 * @date 2024/03/11 10:40
 **/
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    // 自定义异常处理
    @ResponseBody
    @ExceptionHandler(MomoClassException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(MomoClassException e) {
        log.error("系统异常:{}", e.getErrMessage(), e);

        String errMessage = e.getErrMessage();
        RestErrorResponse response = new RestErrorResponse(errMessage);
        return response;
    }

    // 系统自带异常，如数据库连接异常、网络断开等
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(Exception e) {
        log.error("系统异常:{}", e.getMessage(), e);

        RestErrorResponse response = new RestErrorResponse(CommonError.UNKNOW_ERROR.getErrMessage());
        return response;
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item->{
            errors.add(item.getDefaultMessage());
        });

        String errMessage = StringUtils.join(errors, ",");

        log.error("系统异常{}", e.getMessage(), errMessage);
        RestErrorResponse response = new RestErrorResponse(errMessage);
        return response;
    }
}
