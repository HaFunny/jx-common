package com.jx.core.framework.context.impl;

import com.jx.core.framework.context.JxContext;
import com.jx.core.framework.util.CurrentUser;

import java.util.HashMap;
import java.util.Map;

/**
 * @Type: JxContextImpl.java
 * @Author: zhangye
 * @Create_at: 2022/1/18 16:22
 * @Description:
 * @Version: 1.0
 */
public class JxContextImpl implements JxContext {
    private static final long serialVersionUID = -8020614014710506699L;
    private CurrentUser currentUser = new CurrentUser();
    private Map properties = new HashMap();

    public JxContextImpl() {
    }

    @Override
    public CurrentUser getCurrentUser() {
        return this.currentUser;
    }

    @Override
    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void removeCurrentUser() {
        this.currentUser = null;
    }

    @Override
    public Object getProperty(Object obj) {
        return this.properties.get(obj);
    }

    @Override
    public void addProperty(Object key, Object var) {
        this.properties.put(key, var);
    }

    @Override
    public void removeProperty(Object obj) {
        this.properties.remove(obj);
    }

    @Override
    public Map getProperties() {
        return this.properties;
    }

    @Override
    public void setProperties(Map map) {
        this.properties = map;
    }

    @Override
    public void removeAllProperties() {
        this.properties.clear();
    }
}
