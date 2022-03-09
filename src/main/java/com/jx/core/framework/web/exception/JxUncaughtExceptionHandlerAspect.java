package com.jx.core.framework.web.exception;

import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Type: JxUncaughtExceptionHandlerAspect.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 11:22
 * @Description:
 * @Version: 1.0
 */
public class JxUncaughtExceptionHandlerAspect {
    @Autowired
    private JxUncaughtExceptionHandler jxUncaughtExceptionHandler;

    public JxUncaughtExceptionHandlerAspect() {
    }

    public void doBefore(JoinPoint jp) {
        Thread.setDefaultUncaughtExceptionHandler(this.jxUncaughtExceptionHandler);
    }
}
