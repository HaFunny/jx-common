package com.jx.core.framework.context;


import com.jx.core.framework.util.CurrentUser;

import java.io.Serializable;
import java.util.Map;

/**
 * @Type: JxContext.java
 * @Author: zhangye
 * @Create_at: 2022/1/18 16:08
 * @Description:
 * @Version: 1.0
 */
public interface JxContext extends Serializable {
    String JX_CONTEXT_KEY = "JxContext";

    CurrentUser getCurrentUser();

    void setCurrentUser(CurrentUser currentUser);

    void removeCurrentUser();

    Object getProperty(Object obj);

    void addProperty(Object var1, Object var2);

    void removeProperty(Object obj);

    Map getProperties();

    void setProperties(Map map);

    void removeAllProperties();
}
