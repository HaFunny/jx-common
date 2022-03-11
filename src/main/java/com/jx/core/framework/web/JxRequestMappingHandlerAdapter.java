package com.jx.core.framework.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jx.core.framework.context.JxContext;
import com.jx.core.framework.context.JxContextHolder;
import com.jx.core.framework.log.LogContext;
import com.jx.core.framework.util.SeriaUtil;
import com.jx.core.framework.web.exception.AppException;
import com.jx.core.framework.web.exception.ExceptionSeq;
import com.jx.core.framework.web.exception.ParamException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * @Type: JxRequestMappingHandlerAdapter.java
 * @Author: zhangye
 * @Create_at: 2022/1/26 11:13
 * @Description:
 * @Version: 1.0
 */
public class JxRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {
    private Logger log = LoggerFactory.getLogger(JxRequestMappingHandlerAdapter.class);
    static int SUCCESS = 0;
    static int FAIL = -1;
    static int PARAM_VALID_EXCEPTION = -2;
    static String MSG_SUCCESS = "成功";
    static String MSG_FAIL = "未知异常";
    private String LOG_CONTEXT_KEY = "_logContext";
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    ExceptionSeq exceptionSeq;
    static Map<Type, Object> basicTypeMap = new HashMap<>();

    public JxRequestMappingHandlerAdapter() {
    }

    protected boolean supportsInternal(HandlerMethod handlerMethod) {
        this.log.debug("[Jx]调用JxRequestMappingHandlerAdapter.supportsInternal()方法,handlerMethod=" + handlerMethod);
        return AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), JxRestPath.class);

    }

    protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        this.log.debug("[Jx]调用JxRequestMappingHandlerAdapter.handleInternal()方法,handlerMethod=" + handlerMethod);
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        Method method = handlerMethod.getMethod();
        Object hostBean = this.applicationContext.getBean(method.getDeclaringClass());
        String methodType = request.getMethod();
        if (null != method && !methodType.equals("") && (methodType.equalsIgnoreCase("GET") || methodType.equalsIgnoreCase("POST"))) {
            this.handleJxContext(request);
            Object returnObj = null;

            String jsonStr;
            String paramString;
            try {
                Parameter[] ps = method.getParameters();
                if (null != ps && ps.length != 0) {
                    Object[] argList;
                    if (ps.length == 1) {
                        if (methodType.equalsIgnoreCase("GET")) {
                            if (!this.isBasicType(ps[0].getType())) {
                                mv.addObject("code", FAIL);
                                mv.addObject("type", WrapperResponse.ResponseType.TYPE_ERROR.getType());
                                mv.addObject("message", "非基本类型参数无法通过GET请求");
                                return mv;
                            }

                            argList = new Object[1];
                            String queryString = request.getQueryString();
                            if (null != queryString && queryString.length() > 0) {
                                try {
                                    queryString = URLDecoder.decode(queryString, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    this.log.error("请求转码异常", e);
                                }
                            }

                            this.log.debug("queryString=" + queryString);
                            paramString = queryString.substring(queryString.indexOf("=") + 1);
                            argList[0] = this.getObject(ps[0].getType(), paramString);
                            returnObj = method.invoke(hostBean, argList);
                            if (returnObj instanceof WrapperResponse) {
                                mv.addObject("code", ((WrapperResponse) returnObj).getCode());
                                mv.addObject("type", ((WrapperResponse) returnObj).getType());
                                mv.addObject("message", ((WrapperResponse) returnObj).getMessage());
                                mv.addObject("data", ((WrapperResponse) returnObj).getData());
                            } else {
                                mv.addObject("code", SUCCESS);
                                mv.addObject("type", WrapperResponse.ResponseType.TYPE_SUCCESS.getType());
                                mv.addObject("message", MSG_SUCCESS);
                                mv.addObject("data", returnObj);
                            }
                            return mv;
                        }

                        jsonStr = this.getJsonStrFromRequest(request);
                        this.log.debug("jsonStr" + jsonStr);
                        Class<?> typeClass = ps[0].getType();
                        this.log.debug("typeClass = {},isBasicType = {},isArray = {},isList = {},isMap = {}", typeClass, this, isBasicType(typeClass), typeClass.isArray(), typeClass.isAssignableFrom(List.class), typeClass.isAssignableFrom(Map.class));
                        if (this.isBasicType(typeClass)) {
                            Object paramObj = this.getObject(typeClass, jsonStr);
                            returnObj = method.invoke(hostBean, paramObj);
                        } else if (typeClass.isArray()) {
                            JSONArray jsonArray = JSON.parseArray(jsonStr);
                            argList = new Object[1];
                            Object array;
                            if (this.isBasicType(typeClass.getComponentType())) {
                                array = Array.newInstance(typeClass.getComponentType(), jsonArray.size());

                                for (int i = 0; i < jsonArray.size(); ++i) {
                                    Array.set(array, i, jsonArray.get(i));
                                }

                                argList[0] = array;
                                returnObj = method.invoke(hostBean, argList);
                            } else {
                                array = Array.newInstance(typeClass.getComponentType(), jsonArray.size());

                                for (int i = 0; i < jsonArray.size(); ++i) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Object paramObj = JSON.toJavaObject(jsonObject, typeClass.getComponentType());
                                    Array.set(array, i, paramObj);
                                }

                                argList[0] = array;
                                returnObj = method.invoke(hostBean, argList);
                            }
                        } else {
                            ParameterizedType pt;
                            Type type;
                            Type t;

                            if (typeClass.isAssignableFrom(List.class)) {
                                type = ps[0].getParameterizedType();
                                if (type instanceof ParameterizedType) {
                                    pt = (ParameterizedType) type;
                                    t = pt.getActualTypeArguments()[0];
                                    Class<?> clz = Class.forName(t.getTypeName());
                                    JSONArray jsonArray = JSONArray.parseArray(jsonStr);
                                    List list = new ArrayList<>();

                                    for (int i = 0; i < jsonArray.size(); ++i) {
                                        Object paramObj;
                                        if (this.isBasicType(clz)) {
                                            paramObj = jsonArray.getObject(i, t);
                                        } else {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            paramObj = jsonObject.toJavaObject(t);
                                        }

                                        list.add(paramObj);
                                    }

                                    returnObj = method.invoke(hostBean, list);
                                }
                            } else if (typeClass.isAssignableFrom(Map.class)) {
                                type = ps[0].getParameterizedType();
                                if (type instanceof ParameterizedType) {
                                    pt = (ParameterizedType) type;
                                    t = pt.getActualTypeArguments()[0];
                                    Type t1 = pt.getActualTypeArguments()[1];
                                    Class<?> clz0 = Class.forName(t.getTypeName());
                                    Class<?> clz1 = Class.forName(t1.getTypeName());
                                    Map map = (Map) JSONObject.parseObject(jsonStr, typeClass);
                                    Map jmap = new HashMap();

                                    Object kobj;
                                    Object rvobj;
                                    for (Iterator it = map.keySet().iterator(); it.hasNext(); jmap.put(clz0.getConstructor(String.class).newInstance(kobj), rvobj)) {
                                        kobj = it.next();
                                        if (this.isBasicType(clz1)) {
                                            rvobj = map.get(kobj);
                                        } else {
                                            JSONObject vobj = (JSONObject) map.get(kobj);
                                            rvobj = vobj.toJavaObject(t1);
                                        }
                                    }

                                    returnObj = method.invoke(hostBean, jmap);
                                }
                            } else {
                                Object[] argArray = new Object[1];
                                Object paramObj = JSONObject.parseObject(jsonStr, typeClass);
                                argArray[0] = paramObj;
                                returnObj = method.invoke(hostBean, argArray);
                            }
                        }
                    } else {
                        int i;
                        Iterator var;
                        if (methodType.equalsIgnoreCase("GET")) {
                            jsonStr = request.getQueryString();
                            if (null != jsonStr && jsonStr.length() > 0) {
                                try {
                                    jsonStr = URLDecoder.decode(jsonStr, "UTF-8");
                                } catch (Exception e) {
                                    this.log.error("请求转码异常");
                                }
                            }

                            this.log.debug("queryString=" + jsonStr);
                            if (null == jsonStr || jsonStr.trim().equals("")) {
                                mv.addObject("code", FAIL);
                                mv.addObject("type", WrapperResponse.ResponseType.TYPE_ERROR.getType());
                                mv.addObject("message", "请求参数个数不匹配");
                                return mv;
                            }

                            List<String> list = this.convertToList(jsonStr.trim());
                            if (list.size() != ps.length) {
                                mv.addObject("code", FAIL);
                                mv.addObject("type", WrapperResponse.ResponseType.TYPE_ERROR.getType());
                                mv.addObject("message", "请求参数个数不匹配");
                                return mv;
                            }

                            i = 0;
                            argList = new Object[ps.length];
                            for (var = list.iterator(); var.hasNext(); ++i) {
                                String s = (String) var.next();
                                argList[i] = this.getObject(ps[i].getType(), s);
                            }

                            returnObj = method.invoke(hostBean, argList);
                        } else {
                            jsonStr = this.getJsonStrFromRequest(request);
                            JSONObject jsonObj = JSON.parseObject(jsonStr);
                            if (jsonObj.size() != ps.length) {
                                mv.addObject("code", FAIL);
                                mv.addObject("type", WrapperResponse.ResponseType.TYPE_ERROR.getType());
                                mv.addObject("message", "请求参数个数不匹配");
                                return mv;
                            }

                            i = 0;
                            argList = new Object[ps.length];
                            for (var = jsonObj.values().iterator(); var.hasNext(); ++i) {
                                Object o = var.next();
                                argList[i] = o;
                            }

                            returnObj = method.invoke(hostBean, argList);
                        }
                    }
                } else {
                    returnObj = method.invoke(hostBean);
                }
            } catch (Exception e) {
                if (null != e.getCause() && e.getCause() instanceof ParamException) {
                    mv.addObject("code", PARAM_VALID_EXCEPTION);
                    mv.addObject("type", WrapperResponse.ResponseType.TYPE_ERROR.getType());
                    mv.addObject("message", e.getCause().getMessage());
                    return mv;
                }

                jsonStr = "[jxException]";
                LogContext logContext = (LogContext) JxContextHolder.getContext().getProperty(LOG_CONTEXT_KEY);
                paramString = "";
                if (null != logContext && null != logContext.getTraceID()) {
                    paramString = logContext.getTraceID();
                    jsonStr = jsonStr + "[traceID:" + paramString + "]";
                }

                String exseq = exceptionSeq.getExceptionSeq();
                log.error(jsonStr + "调用JxRequestMappingHandlerAdapter.handleInternal()方法,handlerMethod=" + handlerMethod + "出现异常，异常流水号exseq=" + exseq + ",异常堆栈如下：", e);
                if (null != e.getCause() && e.getCause() instanceof AppException) {
                    AppException appEx = (AppException) e.getCause();
                    mv.addObject("code", appEx.getCode());
                    mv.addObject("message", appEx.getMessage() + ",异常流水号：" + exseq);
                } else {
                    mv.addObject("code", FAIL);
                    mv.addObject("message", "调用目标服务异常，请联系管理员后查看后台日志信息，异常流水号：" + exseq);
                }
                mv.addObject("type", WrapperResponse.ResponseType.TYPE_ERROR.getType());
                return mv;
            }

            if (returnObj instanceof WrapperResponse) {
                mv.addObject("code", ((WrapperResponse) returnObj).getCode());
                mv.addObject("type", ((WrapperResponse) returnObj).getType());
                mv.addObject("message", ((WrapperResponse) returnObj).getMessage());
                mv.addObject("data", ((WrapperResponse) returnObj).getData());
            } else {
                mv.addObject("code", SUCCESS);
                mv.addObject("type", WrapperResponse.ResponseType.TYPE_SUCCESS.getType());
                mv.addObject("message", MSG_SUCCESS);
                mv.addObject("data", returnObj);
            }

            return mv;
        } else {
            mv.addObject("code", FAIL);
            mv.addObject("type", WrapperResponse.ResponseType.TYPE_ERROR.getType());
            mv.addObject("message", "请求参数类型异常，当前版本仅支持GET、POST");
            return mv;
        }
    }

    private List<String> convertToList(String queryString) {
        List<String> list = new ArrayList<>();
        queryString = queryString.replaceAll("&amp;", "&");
        String[] qs = queryString.split("&");
        String[] var = qs;
        int length = qs.length;

        for (int i = 0; i < length; ++i) {
            String s = var[i];
            list.add(s.substring(s.indexOf("=") + 1));
        }
        return list;
    }

    private String getJsonStrFromRequest(HttpServletRequest request) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            inputStream.close();
            String jsonStr = result.toString("UTF-8");
            result.close();
            return jsonStr;
        } catch (IOException e) {
            this.log.error("====从request请求输入流中获取数据异常====");
            e.printStackTrace();
            return null;
        }
    }

    private Object getObject(Class<?> clazz, String paramString) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (clazz.isPrimitive()) {
            if (clazz.getSimpleName().equalsIgnoreCase("boolean")) {
                return Boolean.valueOf(paramString);
            }

            if (clazz.getSimpleName().equalsIgnoreCase("char")) {
                return paramString.charAt(0);
            }

            if (clazz.getSimpleName().equalsIgnoreCase("byte")) {
                return Byte.valueOf(paramString);
            }

            if (clazz.getSimpleName().equalsIgnoreCase("short")) {
                return Short.valueOf(paramString);
            }

            if (clazz.getSimpleName().equalsIgnoreCase("int")) {
                return Integer.valueOf(paramString);
            }

            if (clazz.getSimpleName().equalsIgnoreCase("long")) {
                return Long.valueOf(paramString);
            }

            if (clazz.getSimpleName().equalsIgnoreCase("float")) {
                return Float.valueOf(paramString);
            }

            if (clazz.getSimpleName().equalsIgnoreCase("double")) {
                return Double.valueOf(paramString);
            }

            if (clazz.getSimpleName().equalsIgnoreCase("void")) {
                return Void.TYPE;
            }
        }

        if (clazz.getSimpleName().equalsIgnoreCase("Character")) {
            return paramString.charAt(0);
        } else if (clazz.getSimpleName().equalsIgnoreCase("void")) {
            return Void.TYPE;
        } else {
            Constructor c1 = clazz.getConstructor(String.class);
            return c1.newInstance();
        }
    }


    private boolean isBasicType(Class<?> type) {
        if (type.isPrimitive()) {
            return true;
        } else {
            return basicTypeMap.containsKey(type);
        }
    }

    private void handleJxContext(HttpServletRequest request) {
        JxContext jxContext = JxContextHolder.getContext();
        if (null == jxContext
                || null == jxContext.getCurrentUser()
                || null == jxContext.getCurrentUser().getUserAccountId()
                && jxContext.getProperties().isEmpty()) {
            String strontext = (String) request.getAttribute("JxContext");
            if (StringUtils.isNotBlank(strontext)) {
                SeriaUtil<JxContext> seriaUtil = new SeriaUtil<>();
                jxContext = seriaUtil.derialFromBase64(strontext);
                JxContextHolder.setContext(jxContext);
            }
        }
    }

    static {
        basicTypeMap.put(String.class, (Object) null);
        basicTypeMap.put(Number.class, (Object) null);
        basicTypeMap.put(Boolean.class, (Object) null);
        basicTypeMap.put(Character.class, (Object) null);
        basicTypeMap.put(Byte.class, (Object) null);
        basicTypeMap.put(Short.class, (Object) null);
        basicTypeMap.put(Integer.class, (Object) null);
        basicTypeMap.put(Long.class, (Object) null);
        basicTypeMap.put(Float.class, (Object) null);
        basicTypeMap.put(Double.class, (Object) null);
    }
}
