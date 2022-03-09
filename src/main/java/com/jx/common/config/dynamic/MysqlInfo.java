package com.jx.common.config.dynamic;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Type: MysqlInfo.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 14:46
 * @Description:
 * @Version: 1.0
 */
@Data
@Component("mysqlInfo")
@ConfigurationProperties(prefix = "db.druid.mysql")
public class MysqlInfo {
    private String driverClassName;
    private String url;
    private String userName;
    private String password;
    private int initialSize = 1;
    private int minldle = 1;
    private int maxActive = 20;
    private long maxWait = 60000;
    private long timeBetweenEvictionRunsMillis = 60000;
    private long minEvictableldleTimeMillis = 300000;
    private String validationQuery;
    private boolean testWhileldle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean poolPreparedStatements = false;
    private int maxPoolPreparedStatementPerConnectionSize = 20;
}
