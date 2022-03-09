package com.jx.core.framework.web.exception;

import com.jx.core.framework.context.JxContextHolder;
import com.jx.core.framework.log.LogContext;
import com.jx.core.framework.web.WrapperResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Type: JxHandlerExceptionResolver.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 11:01
 * @Description:
 * @Version: 1.0
 */
public class JxHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
    private Logger log = LoggerFactory.getLogger(JxHandlerExceptionResolver.class);
    private String viewType = "json";
    private String defaultErrorView;
    private String defaultErrorMessage;
    private Map<Integer, String> exceptionMappings = new HashMap<>();
    private String LOG_CONTEXT_KEY = "_logContext";
    @Autowired
    ExceptionSeq exceptionSeq;

    public JxHandlerExceptionResolver() {
    }

    public int getOrder() {
        return 0;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        ModelAndView mv = null;
        Integer errorCode = WrapperResponse.FAIL;
        LogContext logContext = (LogContext) JxContextHolder.getContext().getProperty(LOG_CONTEXT_KEY);
        String traceID = "";
        String traceInfo = "[JxException]";
        if (null != logContext && null != logContext.getTraceId()) {
            traceID = logContext.getTraceId();
            traceInfo = traceInfo + "[traceID:" + traceID + "]";
        }

        String exseq = exceptionSeq.getExceptionSeq();
        if (e instanceof AppException) {
            int code = ((AppException) e).getCode();
            errorCode = code;
            log.warn(traceInfo + "异常流水号exseq=" + exseq + ",错误信息:" + e.getMessage());
        } else if (null == e.getCause()) {
            log.error(traceInfo + "异常流水号exseq=" + exseq + ",错误信息:" + e.getMessage(), e);
        } else {
            log.error(traceInfo + "异常流水号exseq=" + exseq + ",错误信息:" + e.getMessage(), e.getCause());
        }

        if ("page".equalsIgnoreCase(viewType)) {
            String viewName = defaultErrorView;
            if (null != exceptionMappings && exceptionMappings.containsKey(errorCode)) {
                viewName = (String) exceptionMappings.get(errorCode);
            }

            mv = new ModelAndView();
            mv.setViewName(viewName);
        } else {
            mv = new ModelAndView(new MappingJackson2JsonView());
        }

        mv.addObject("code", errorCode);
        if (e instanceof AppException && null != e.getMessage() && e.getMessage().length() > 0) {
            mv.addObject("message", e.getMessage() + ",异常流水号:" + exseq);
        } else if (null != defaultErrorMessage && !"".equals(defaultErrorMessage)) {
            mv.addObject("message", defaultErrorMessage + ",异常流水号:" + exseq);
        } else {
            mv.addObject("message", e.getMessage() + ",异常流水号:" + exseq);
        }

        return mv;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public void setDefaultErrorView(String defaultErrorView) {
        this.defaultErrorView = defaultErrorView;
    }

    public void setDefaultErrorMessage(String defaultErrorMessage) {
        this.defaultErrorMessage = defaultErrorMessage;
    }

    public void setExceptionMappings(Map<Integer, String> exceptionMappings) {
        this.exceptionMappings.putAll(exceptionMappings);
    }

    public void addExceptionMappings(Integer errorCode, String errorView) {
        this.exceptionMappings.put(errorCode, errorView);
    }
}
