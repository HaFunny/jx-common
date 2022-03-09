package com.jx.core.framework.web;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Type: JxRestPath.java
 * @Author: zhangye
 * @Create_at: 2022/1/26 11:36
 * @Description:
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface JxRestPath {
    String value();

    RequestMethod[] method() default {};
}
