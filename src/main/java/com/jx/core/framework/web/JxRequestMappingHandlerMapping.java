package com.jx.core.framework.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @Type: JxRequestMappingHandlerMapping.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 11:30
 * @Description:
 * @Version: 1.0
 */
public class JxRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private Logger log = LoggerFactory.getLogger(JxRequestMappingHandlerMapping.class);
    private int order = 5;

    public JxRequestMappingHandlerMapping() {
    }

    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, JxRestPath.class);
    }

    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        JxRestPath jp_clz = (JxRestPath) AnnotatedElementUtils.findMergedAnnotation(handlerType, JxRestPath.class);
        JxRestPath jp_method = (JxRestPath) AnnotatedElementUtils.findMergedAnnotation(method, JxRestPath.class);
        if (null != jp_clz && null != jp_method) {
            String clz_path = null == jp_clz.value() ? "" : jp_clz.value().trim();
            String method_path = null == jp_method.value() ? "" : jp_method.value().trim();
            RequestMethod[] rm = jp_method.method();
            String url;
            if ("/".equals(clz_path) && method_path.startsWith("/")) {
                url = method_path;
            } else {
                url = clz_path + method_path;
            }

            log.info("registerHandler,url=" + url + ",method=" + method);
            RequestMappingInfo.Builder builder = RequestMappingInfo.paths(new String[]{url});
            if (null != rm && rm.length > 0) {
                builder.methods(rm);
            } else {
                builder.methods(new RequestMethod[]{RequestMethod.GET, RequestMethod.POST});
            }

            return builder.build();
        } else {
            return null;
        }
    }
}
