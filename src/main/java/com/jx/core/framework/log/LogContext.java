package com.jx.core.framework.log;

import java.io.Serializable;

/**
 * @Type: LogContext.java
 * @Author: zhangye
 * @Create_at: 2022/1/18 16:37
 * @Description: 日志上下文
 * @Version: 1.0
 */
public class LogContext implements Serializable {

    private static final long serialVersionUID = 193018213879621871L;
    private String traceID;

    public LogContext() {
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    @Override
    public String toString() {
        return "LogContext [" +
                "traceID='" + traceID + '\'' +
                ']';
    }
}
