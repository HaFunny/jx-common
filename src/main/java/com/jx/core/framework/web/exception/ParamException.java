package com.jx.core.framework.web.exception;


/**
 * @Type: ParamException.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 11:24
 * @Description:
 * @Version: 1.0
 */
public class ParamException extends AppException {
    private static final long serialVersionUID = -6732352092728027208L;
    static int error_code = -2;

    public ParamException() {
    }

    public ParamException(int code) {
        super(code);
    }

    public ParamException(String message) {
        super(error_code, message);
    }

    public ParamException(int code, String message) {
        super(code, message);
    }

    public ParamException(Throwable cause) {
        super(cause);
    }

    public ParamException(String message, Throwable cause) {
        super(error_code, message, cause);
    }

    public ParamException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
