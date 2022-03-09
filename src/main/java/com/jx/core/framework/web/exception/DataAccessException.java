package com.jx.core.framework.web.exception;


/**
 * @Type: DataAccessException.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 11:00
 * @Description:
 * @Version: 1.0
 */
public class DataAccessException extends AppException {
    private static final long serialVersionUID = -2236548298229251294L;

    public DataAccessException() {
    }

    public DataAccessException(int code) {
        super(code);
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(int code, String message) {
        super(code, message);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
