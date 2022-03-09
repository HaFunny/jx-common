package com.jx.common.config.dynamic;

import com.alibaba.druid.pool.DruidDataSource;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

/**
 * @Type: DataSourceConfig.java
 * @Author: zhangye
 * @Create_at: 2022/3/9 14:53
 * @Description:
 * @Version: 1.0
 */
@Configuration
public class DataSourceConfig {
    public static final String TRAN_PG = "pg_transaction";
    public static final String TRAN_MYSQL = "mysql_transaction";

    @Autowired
    private StringEncryptor stringEncryptor;

    @Bean(DbEnum.DB_MYSQL)
    public DruidDataSource dataSourceMysql(MysqlInfo mysqlInfo) {
        DruidDataSource dataSource = new DruidDataSource();
        mysqlInfo.setPassword(this.decrypt(mysqlInfo.getPassword()));
        BeanUtils.copyProperties(mysqlInfo, dataSource);
        try {
            dataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Bean(DbEnum.DB_PG)
    public DruidDataSource dataSourcePg(PgInfo pgInfo) {
        DruidDataSource dataSource = new DruidDataSource();
        pgInfo.setPassword(this.decrypt(pgInfo.getPassword()));
        BeanUtils.copyProperties(pgInfo, dataSource);
        try {
            dataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Bean(name = TRAN_PG)
    public PlatformTransactionManager pgTransactionManager(@Qualifier(DbEnum.DB_PG) DruidDataSource dataSource) {
        DataSourceTransactionManager datasourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return datasourceTransactionManager;
    }
    @Bean(name = TRAN_MYSQL)
    public PlatformTransactionManager mysqlTransactionManager(@Qualifier(DbEnum.DB_MYSQL) DruidDataSource dataSource) {
        DataSourceTransactionManager datasourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return datasourceTransactionManager;
    }

    private String decrypt(String str) {
        if (!PropertyValueEncryptionUtils.isEncryptedValue(str)) {
            return str;
        }
        return stringEncryptor.decrypt(str);
    }

}
