package com.util.codegenerate.common.aspect;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.util.codegenerate.common.aspect.annotation.SelectDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class SelectDataSourceAspect {

    @Around(value = "@annotation(selectDataSource)&& args(..,user,ds)", argNames = "joinPoint,selectDataSource,ds,user") // 指定环绕通知应用于带有 LogExecutionTime 注解的方法
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, SelectDataSource selectDataSource,Object ds,Object user) throws Throwable {
        try{
            DynamicDataSourceContextHolder.push(user+"-"+ds.toString());
            return joinPoint.proceed();
        }finally {
            DynamicDataSourceContextHolder.clear();
        }

    }
}
