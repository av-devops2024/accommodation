package com.devops.accommodation.aspect;

import com.devops.accommodation.service.implementation.LogClientService;
import com.google.gson.Gson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnExpression("${aspect.enabled:true}")
public class ExecutionTimeAdvice {

    @Autowired
    private LogClientService logClientService;

    @Autowired
    private Gson gson;

    @Around("@annotation(executionTime)")
    public Object executionTime(ProceedingJoinPoint point, TrackExecutionTime executionTime) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object object = point.proceed();
        long endTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) point.getSignature();
        String[] className = signature.getDeclaringTypeName().split("\\.");
        logClientService.sendTrace(className[className.length-1], signature.getName(),
                (int) (endTime-startTime), gson.toJson(point.getArgs()));
        return object;
    }
}
