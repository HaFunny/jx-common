package com.jx.common.config.dynamic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Type: DataSource.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 14:32
 * @Description: 多数据源配置
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataSource {
    String value() default DbEnum.DB_MYSQL;
}
