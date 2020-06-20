package com.jn.sqlhelper.examples.apache.dbutils.config;

import com.jn.sqlhelper.apachedbutils.QueryRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SqlsConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    @Bean
    public QueryRunner queryRunner(DataSource dataSource) {
        return new QueryRunner(dataSource);
    }
}
