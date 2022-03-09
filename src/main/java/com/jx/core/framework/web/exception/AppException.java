package com.jx.core.framework.web.exception;


/**
 * @Type: AppException.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 10:49
 * @Description:
 * @Version: 1.0
 */
public class AppException extends RuntimeException {
    private static final long serialVersionUID = -2351569685595408600L;
    int code = -1;

    public AppException() {
    }

    public AppException(int code) {
        this.code = code;
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(int code, String message) {
        super(message);
        this.code = code;
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
