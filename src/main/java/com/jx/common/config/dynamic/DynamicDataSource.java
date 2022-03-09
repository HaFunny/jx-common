package com.jx.common.config.dynamic;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @Type: DynamicDataSource.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 15:26
 * @Description:
 * @Version: 1.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDb();
    }
}
