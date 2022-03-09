package com.jx.core.framework.log;

import com.alibaba.fastjson.JSONObject;
import com.jx.core.framework.context.JxContextHolder;
import com.jx.core.framework.util.CurrentUser;
import com.jx.core.framework.util.IPUtil;
import com.jx.core.framework.util.IdWorker;
import com.jx.core.framework.web.WrapperResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * @Type: JxLogHandler.java
 * @Author: zhangye
 * @Create_at: 2022/1/18 15:41
 * @Description: 统一日志处理器
 * @Version: 1.0
 */
public class JxLogHandler {
    private static final Logger log = LoggerFactory.getLogger(JxLogHandler.class);
    private static final String CALL_FROM_JX_REST_PATH_CONTROLLER = "JxRestPathController";
    private static final String CALL_FROM_CONROLLER = "controller";
    private static final String CALL_FROM_RPC = "rpc";
    private static final String LOG_CONTEXT_KEY = "_logContext";
    private static final String MULTIPART_FORM_DATA = "multipart/form_data";
    private static final String LOG_TRACEID = "traceId";
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${definition.business.id:0}")
    private String buisnessId;
    private static ThreadLocal<LogInfo> logContextHolder = new ThreadLocal<LogInfo>();

    public JxLogHandler() {
    }

    public void doControllerBefore(JoinPoint joinPoint) {
        log.debug("JxLogHandler.doControllerBefore");

        try {
            LogInfo logInfo = new LogInfo();
            LogContext logContext = (LogContext) JxContextHolder.getContext().getProperty(LOG_CONTEXT_KEY);
            if (null == logContext) {
                logInfo.setTraceId(this.generateTraceId());
            } else {
                logInfo.setTraceId(logContext.getTraceId());
            }

            MDC.put(LOG_TRACEID, logInfo.getTraceId());
            CurrentUser currentUser = JxContextHolder.getContext().getCurrentUser();
            logInfo.setUserAccountId(currentUser.getUserAccountId());//用户账号id
            //请求时间 精确到毫秒
            logInfo.setRequestTime(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
            packHttpLogInfo(logInfo, joinPoint);//http请求日志信息
            logInfo.setCallFrom(CALL_FROM_CONROLLER);
            doBefore(logInfo);
        } catch (IllegalArgumentException e) {
            log.error("JxLogHandler.doControllerBefore.exception", e);
        }

    }

    public void doServiceBefore(JoinPoint joinPoint) {
        log.debug("JxLogHandler.doServiceBefore");

        try {
            LogInfo logInfo = this.getLogInfo4Service();
            String callFrom = logInfo.getCallFrom();
            log.debug("callFrom:{}", callFrom);
            if (CALL_FROM_CONROLLER.equals(callFrom)) {
                return;
            }

            if (CALL_FROM_RPC.equals(callFrom)) {
                MDC.put(LOG_TRACEID, logInfo.getTraceId());
                //请求时间 精确到毫秒
                logInfo.setRequestTime(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                packRpcLogInfo(logInfo, joinPoint);//rpc请求日志信息
            } else if (CALL_FROM_JX_REST_PATH_CONTROLLER.equals(callFrom)) {
                MDC.put(LOG_TRACEID, logInfo.getTraceId());
                //请求时间 精确到毫秒
                logInfo.setRequestTime(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
                packHttpLogInfo(logInfo, joinPoint);//http请求日志信息
            }

            CurrentUser currentUser = JxContextHolder.getContext().getCurrentUser();
            logInfo.setUserAccountId(currentUser.getUserAccountId());
            doBefore(logInfo);
        } catch (IllegalArgumentException e) {
            log.error("JxLogHandler.doServiceBefore.exception", e);
        }
    }

    private static void packRpcLogInfo(LogInfo logInfo, JoinPoint joinPoint) {
        logInfo.setRequestUrl("");
        logInfo.setClientIP("");
        logInfo.setRequestMethod(CALL_FROM_RPC);
        logInfo.setClassMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        if (null != joinPoint.getArgs() && joinPoint.getArgs().length > 0) {
            String requestParams = Arrays.toString(joinPoint.getArgs());
            logInfo.setRequestParams(requestParams);
        }
    }

    private LogInfo getLogInfo4Service() {
        LogInfo logInfo = logContextHolder.get();
        if (null != logInfo) {
            return logInfo;
        } else {
            LogContext logContext = (LogContext) JxContextHolder.getContext().getProperty(LOG_CONTEXT_KEY);
            if (null != logContext) {
                logInfo = new LogInfo();
                logInfo.setTraceId(logContext.getTraceId());
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (null != attributes) {
                    logInfo.setCallFrom(CALL_FROM_JX_REST_PATH_CONTROLLER);
                } else {
                    logInfo.setCallFrom(CALL_FROM_RPC);
                }
            }

            if (null == logInfo) {
                logInfo = new LogInfo();
                logInfo.setTraceId(this.generateTraceId());
                logInfo.setCallFrom(CALL_FROM_JX_REST_PATH_CONTROLLER);
            }

            return logInfo;
        }
    }

    public void doControllerAfterReturning(Object obj) {
        log.debug("JxLogHandler.doControllerAfterReturning");

        try {
            LogInfo logInfo = logContextHolder.get();
            if (null == logInfo) {
                return;
            }

            doAfterReturning(obj, logInfo);
        } catch (Exception e) {
            log.error("JxLogHandler.doControllerAfterReturning.exception", e);
        }
    }

    public void doServiceAfterReturning(Object obj) {
        log.debug("JxLogHandler.doServiceAfterReturing");

        try {
            LogInfo logInfo = logContextHolder.get();
            if (null == logInfo) {
                return;
            }

            if (CALL_FROM_CONROLLER.equals(logInfo.getCallFrom())) {
                return;
            }

            doAfterReturning(obj, logInfo);
        } catch (Exception e) {
            log.error("JxLogHandler.doServiceAfterReturing.exception", e);
        }
    }

    private static void doAfterReturning(Object obj, LogInfo logInfo) {
        if (null != obj && obj.getClass().isAssignableFrom(WrapperResponse.class)) {
            logInfo.setCode(((WrapperResponse) obj).getCode());
            log.debug(JSONObject.toJSONString(obj));
        }

        log.debug("=====[response data]=====");
        log.info(logInfo.getLogString());
        MDC.remove(LOG_TRACEID);
    }

    public Object doControllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("JxLogHandler.doControllerAround");
        long startTime = System.currentTimeMillis();
        Object object = joinPoint.proceed();

        try {
            LogInfo logInfo = logContextHolder.get();
            if (null == logInfo) {
                return object;
            }

            doAround(startTime, logInfo);
        } catch (Exception e) {
            log.error("JxLogHandler.doControllerAround.exception", e);
        }

        return object;
    }

    public Object doServiceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("JxLogHandler.doServiceAround");
        long startTime = System.currentTimeMillis();
        Object object = joinPoint.proceed();

        try {
            LogInfo logInfo = logContextHolder.get();
            if (null == logInfo) {
                return object;
            }

            doAround(startTime, logInfo);
        } catch (Exception e) {
            log.error("JxLogHandler.doServiceAround.exception", e);
        }

        return object;
    }

    private static void doAround(long startTime, LogInfo logInfo) {
        long timeConsuming = System.currentTimeMillis() - startTime;
        log.debug("[timeConsuming] => {}", timeConsuming);
        logInfo.setTimeConsuming(timeConsuming);
    }

    private static void doBefore(LogInfo logInfo) {
        log.debug("=====[request data]=====");
        log.debug(logInfo.toString());
        log.debug(logInfo.getLogString());
        logContextHolder.set(logInfo);
        JxContextHolder.getContext().addProperty(LOG_CONTEXT_KEY, generateLogContext(logInfo));
    }

    private static Object generateLogContext(LogInfo logInfo) {
        LogContext logContext = new LogContext();
        logContext.setTraceId(logInfo.getTraceId());
        return logContext;
    }

    private static void packHttpLogInfo(LogInfo logInfo, JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != attributes && null != attributes.getRequest()) {
            HttpServletRequest request = attributes.getRequest();
            logInfo.setRequestUrl(request.getRequestURL().toString());//请求路径
            logInfo.setClientIP(IPUtil.getIpAddress(request));//客户端Ip
            logInfo.setRequestMethod(request.getMethod());//请求方式 get | post | delete | put ......
            //被增强目标 全类名.方法名
            logInfo.setClassMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            if (null != joinPoint.getArgs() && joinPoint.getArgs().length > 0) {
                String requestParams = "";
                log.debug("request.getMethod()=" + request.getMethod() + "; request.getContentType()=" + request.getContentType());
                if ("GET".equalsIgnoreCase(request.getMethod())) {
                    requestParams = Arrays.toString(joinPoint.getArgs());
                } else {
                    try {
                        if (null == request.getContentType() || request.getContentType().indexOf(MULTIPART_FORM_DATA) == -1) {
                            requestParams = JSONObject.toJSONString(joinPoint.getArgs()[0]);
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                        requestParams = "";
                    }
                }

                logInfo.setRequestParams(requestParams);
            }
        }
    }

    private String generateTraceId() {
        //雪花算法，中间十位 为业务id + 机器id(id由机器ip地址最后一组数字指定)
        String localIP = null;
        try {
            localIP = IPUtil.getLocalIP();
        } catch (UnknownHostException | SocketException e) {
            log.error("========获取Ip异常========", e);
            localIP = "0";
        }
        String snowId = String.valueOf(new IdWorker(Long.parseLong(buisnessId), Long.parseLong(localIP.substring(localIP.lastIndexOf(".") + 1))).nextId());

        if (null != this.applicationName && this.applicationName.length() > 10) {
            this.applicationName = this.applicationName.substring(0, 10);
        }
        return this.applicationName + "-" + snowId;
    }
}
