package com.blog.exception;

import com.blog.common.response.ResultCode;

/**
 * 断言处理类，用于抛出各种API异常
 */
public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(ResultCode errorCode) {
        throw new ApiException(errorCode);
    }
} 