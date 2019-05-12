package com.fjn.helper.examples.mybatis.spring.boot.config;

import com.fjn.helper.sql.dialect.conf.SQLInstrumentConfig;
import com.fjn.helper.sql.mybatis.plugins.pagination.PaginationPluginConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "sqlhelper.mybatis")
public class SqlHelperMybatisProperties {

    @NestedConfigurationProperty
    private SQLInstrumentConfig instrumentor = new SQLInstrumentConfig();
    @NestedConfigurationProperty
    private PaginationPluginConfig pagination = new PaginationPluginConfig();

    public SQLInstrumentConfig getInstrumentor() {
        return instrumentor;
    }

    public void setInstrumentor(SQLInstrumentConfig instrumentor) {
        this.instrumentor = instrumentor;
    }

    public PaginationPluginConfig getPagination() {
        return pagination;
    }

    public void setPagination(PaginationPluginConfig pagination) {
        this.pagination = pagination;
    }
}
