package com.jn.sqlhelper.examples.apache.dbutils.config;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.apachedbutils.QueryRunner;
import org.apache.commons.dbutils.QueryLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class SqlsConfig {
    @Bean("sqlMap")
    public Map<String, String> sqlMap(){
        try {
            return QueryLoader.instance().load("sql.properties");
        }catch (Throwable ex){
            return Collects.emptyHashMap();
        }
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    @Bean
    public QueryRunner queryRunner(DataSource dataSource){
        return new QueryRunner(dataSource);
    }
}
