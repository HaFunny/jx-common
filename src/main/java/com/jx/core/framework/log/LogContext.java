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
    private String traceId;

    public LogContext() {
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "LogContext [" +
                "traceId='" + traceId + '\'' +
                ']';
    }
}
