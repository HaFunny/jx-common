package com.jx.common.config.dynamic;

/**
 * @Type: DbEnum.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 14:34
 * @Description: 数据源枚举
 * @Version: 1.0
 */
public class DbEnum {
    private DbEnum() {
    }

    public static final String DB_PG = "pg";
    public static final String DB_MYSQL = "mysql";
    //定时任务数据源
    public static final String DB_TASK = "task";
}
