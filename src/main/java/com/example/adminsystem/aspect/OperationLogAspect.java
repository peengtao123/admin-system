package com.example.adminsystem.aspect;

import com.example.adminsystem.entity.OperationLog;
import com.example.adminsystem.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    @Pointcut("execution(* com.example.adminsystem.controller.*.*(..)) && !execution(* com.example.adminsystem.controller.OperationLogController.*(..))")
    public void operationLog() {}

    @AfterReturning(pointcut = "operationLog()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        OperationLog log = new OperationLog();
        log.setIp(request.getRemoteAddr());
        log.setMethod(joinPoint.getSignature().getName());
        log.setParams(Arrays.toString(joinPoint.getArgs()));
        log.setCreateTime(LocalDateTime.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            log.setUsername(authentication.getName());
        }

        String operation = "未知操作";
        String method = request.getMethod();
        switch (method) {
            case "POST":
                if (joinPoint.getSignature().getName().contains("save")) {
                    operation = "添加/修改操作";
                }
                break;
            case "DELETE":
                operation = "删除操作";
                break;
            case "PUT":
                operation = "更新操作";
                break;
            case "GET":
                operation = "查询操作";
                break;
        }
        log.setOperation(operation);

        operationLogService.save(log);
    }
}