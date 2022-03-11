package com.jx.core.framework.log;


import java.io.Serializable;

/**
 * @Type: LogInfo.java
 * @Author: zhangye
 * @Create_at: 2022/1/18 15:48
 * @Description: 日志实体
 * @Version: 1.0
 */
public class LogInfo implements Serializable {

    private static final long serialVersionUID = 1464110783106667856L;
    private String traceID;
    private String userAccountId;
    private String requestTime;
    private String requestUrl;
    private String requestMethod;
    private String clientIP;
    private String classMethod;
    private String requestParams;
    private Long timeConsuming;
    private String callFrom;
    private Integer code;

    public LogInfo() {
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getClassMethod() {
        return classMethod;
    }

    public void setClassMethod(String classMethod) {
        this.classMethod = classMethod;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public Long getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(Long timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getCallFrom() {
        return callFrom;
    }

    public void setCallFrom(String callFrom) {
        this.callFrom = callFrom;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "LogInfo[" +
                "traceID='" + traceID + '\'' +
                ", userAccountId='" + userAccountId + '\'' +
                ", requestTime='" + requestTime + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", clientIP='" + clientIP + '\'' +
                ", classMethod='" + classMethod + '\'' +
                ", requestParams='" + requestParams + '\'' +
                ", timeConsuming=" + timeConsuming +
                ", callFrom='" + callFrom + '\'' +
                ", code=" + code +
                ']';
    }

    public String getLogString() {
        StringBuilder sb = new StringBuilder();
        String separator = "|";
        sb.append(this.traceID)
                .append(separator)
                .append(this.clientIP)
                .append(separator)
                .append(this.userAccountId)
                .append(separator)
                .append(this.requestTime)
                .append(separator)
                .append(this.requestUrl)
                .append(separator)
                .append(this.requestMethod)
                .append(separator)
                .append(this.classMethod)
                .append(separator)
                .append(this.requestParams)
                .append(separator)
                .append(this.timeConsuming)
                .append(separator)
                .append(this.code);
        return sb.toString();
    }
}
