package com.util.codegenerate.common.aspect;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.util.codegenerate.common.aspect.annotation.SelectBaseDatasource;
import com.util.codegenerate.common.aspect.annotation.SelectDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class SelectBaseDatasourceAspect {
    @Around(value = "@annotation(selectBaseDatasource)", argNames = "joinPoint,selectBaseDatasource") // 指定环绕通知应用于带有 LogExecutionTime 注解的方法
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, SelectBaseDatasource selectBaseDatasource) throws Throwable {
        try{
            DynamicDataSourceContextHolder.push("w51sa35Mz9AW");
            return joinPoint.proceed();
        }finally {
            DynamicDataSourceContextHolder.clear();
        }

    }
}
