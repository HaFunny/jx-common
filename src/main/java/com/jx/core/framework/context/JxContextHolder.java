package com.jx.core.framework.context;

import com.jx.core.framework.context.impl.JxContextImpl;

/**
 * @Type: JxContextHolder.java
 * @Author: zhangye
 * @Create_at: 2022/1/18 16:05
 * @Description:
 * @Version: 1.0
 */
public class JxContextHolder {
    private static ThreadLocal contextHolder = new ThreadLocal<>();

    public JxContextHolder() {
    }

    public static void setContext(JxContext jxContext) {
        contextHolder.set(jxContext);
    }

    public static JxContext getContext() {
        Object obj = (JxContext) contextHolder.get();
        if (null == obj) {
            obj = new JxContextImpl();
            setContext((JxContext) obj);
        }

        return (JxContext) obj;
    }
}
