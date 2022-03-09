package com.jx.core.framework.web.exception;


/**
 * @Type: BusinessException.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 10:52
 * @Description:
 * @Version: 1.0
 */
public class BusinessException extends AppException {
    private static final long serialVersionUID = -684010181075413551L;

    public BusinessException() {
    }

    public BusinessException(int code) {
        super(code);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int code, String message) {
        super(code, message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

}
