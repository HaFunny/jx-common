package com.jx.core.framework.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Type: JxUncaughtExceptionHandler.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 11:20
 * @Description:
 * @Version: 1.0
 */
public class JxUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Logger log = LoggerFactory.getLogger(JxUncaughtExceptionHandler.class);

    public JxUncaughtExceptionHandler() {
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("捕获到异常：线程名[" + t.getName() + "]，异常名[" + e + "]");
        e.printStackTrace();
    }
}
