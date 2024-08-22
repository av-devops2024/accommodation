package com.devops.accommodation.aspect;

import com.devops.accommodation.service.implementation.LogClientService;
import com.google.gson.Gson;
import ftn.devops.db.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        List<Object> params = new ArrayList<>();
        Arrays.stream(point.getArgs()).forEach(param -> {
            params.add((param.getClass().toString().contains("User")) ? ((User)param).getId() : param);
        });
        logClientService.sendTrace(className[className.length-1], signature.getName(),
                (int) (endTime-startTime), gson.toJson(params));
        return object;
    }
}
