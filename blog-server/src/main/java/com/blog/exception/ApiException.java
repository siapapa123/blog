package com.blog.exception;

import com.blog.common.response.ResultCode;
import lombok.Getter;

/**
 * 自定义API异常
 */
@Getter
public class ApiException extends RuntimeException {
    private ResultCode errorCode;

    public ApiException(ResultCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

}