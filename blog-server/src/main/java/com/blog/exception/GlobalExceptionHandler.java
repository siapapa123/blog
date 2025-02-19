package com.blog.exception;

import com.blog.common.response.ApiResponse;
import com.blog.common.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    public ApiResponse<Object> handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return ApiResponse.failed(e.getErrorCode());
        }
        return ApiResponse.failed(null);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return ApiResponse.failed(ResultCode.VALIDATE_FAILED);
    }

    @ExceptionHandler(value = BindException.class)
    public ApiResponse<Object> handleValidException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return ApiResponse.failed(ResultCode.FAILED);
    }

    /**
     * 处理Spring Security的认证异常
     */
    @ExceptionHandler({
            BadCredentialsException.class,
            AuthenticationException.class
    })
    public ApiResponse<String> handleAuthenticationException(AuthenticationException e) {
        log.error("认证失败", e);
        return ApiResponse.failed(ResultCode.UNAUTHORIZED);
    }

    /**
     * 处理访问权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error("没有访问权限", e);
        return ApiResponse.failed(ResultCode.FORBIDDEN);
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception e) {
        log.error("系统异常", e);
        return ApiResponse.failed(ResultCode.FAILED);
    }
} 