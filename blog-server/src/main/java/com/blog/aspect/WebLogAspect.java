package com.blog.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.blog.common.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class WebLogAspect {

    private final ObjectMapper objectMapper;

    public WebLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("execution(* com.blog.controller..*.*(..))")
    public void webLog() {}

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        String userInfo = userId != null ? "userId:" + userId : "未登录用户";
        
        // 打印请求信息
        log.info("========================================== Start ==========================================");
        log.info("User Info      : {}", userInfo);
        log.info("URL            : {}", request.getRequestURL().toString());
        log.info("HTTP Method    : {}", request.getMethod());
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        log.info("IP             : {}", request.getRemoteAddr());
        log.info("Request Args   : {}", Arrays.toString(joinPoint.getArgs()));

        Object result;
        try {
            result = joinPoint.proceed();
            // 打印响应信息
            log.info("Response       : {}", objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.error("Request Error  : {}", e.getMessage());
            throw e;
        } finally {
            log.info("Time Cost      : {} ms", System.currentTimeMillis() - startTime);
            log.info("=========================================== End ===========================================");
        }
        
        return result;
    }
} 