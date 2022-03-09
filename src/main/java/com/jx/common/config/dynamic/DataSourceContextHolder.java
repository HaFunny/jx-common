package com.jx.common.config.dynamic;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

/**
 * @Type: DataSourceContextHolder.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 15:17
 * @Description:
 * @Version: 1.0
 */
@Component
public class DataSourceContextHolder {
    private DataSourceContextHolder() {
    }

    private String dbType;
    private LinkedList<String> dbTypeStack = Lists.newLinkedList();

    private static final ThreadLocal<DataSourceContextHolder> CONTEXT = ThreadLocal.withInitial(DataSourceContextHolder::new);

    static void setDb(String dbType) {
        DataSourceContextHolder holder = CONTEXT.get();
        String preType = holder.getDbType();
        if (StringUtils.isNotBlank(preType)) {
            holder.getDbTypeStack().offer(preType);
        }
        if (StringUtils.isBlank(dbType)) {
            holder.setDbType(DbEnum.DB_MYSQL);
        } else {
            holder.setDbType(dbType);
        }
    }

    static String getDb() {
        return CONTEXT.get().getDbType();
    }

    static void clearDb() {
        DataSourceContextHolder holder = CONTEXT.get();
        String preDbType = holder.getDbTypeStack().poll();
        if (StringUtils.isBlank(preDbType)) {
            CONTEXT.remove();
        } else {
            holder.setDbType(preDbType);
        }
    }

    public String getDbType() {
        return dbType;
    }

    public LinkedList<String> getDbTypeStack() {
        return dbTypeStack;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
