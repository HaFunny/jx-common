package com.jx.common.config.dynamic;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @Type: DynamicDataSourceAspect.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 15:27
 * @Description:
 * @Version: 1.0
 */
@Slf4j
@Component
@Aspect
public class DynamicDataSourceAspect {
    @Pointcut(value = "@annotation(com.jx.common.config.dynamic.DataSource)")
    public void pointCut() {
    }

    @Before(value = "pointCut()")
    public void beforeSwitchDataSource(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        DataSource dataSource = signature.getMethod().getAnnotation(DataSource.class);
        String dataSourceValue = dataSource.value();
        DataSourceContextHolder.setDb(dataSourceValue);
        log.debug(point.getTarget().getClass().getSimpleName() + ":::" + point.getSignature().getName() + ":::" + dataSource.value());
    }

    @After(value = "pointCut()")
    public void afterSwitchDataSource(JoinPoint point) {
        DataSourceContextHolder.clearDb();
    }
}
