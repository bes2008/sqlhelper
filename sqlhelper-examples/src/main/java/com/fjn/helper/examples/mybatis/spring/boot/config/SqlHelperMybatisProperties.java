package com.fjn.helper.examples.mybatis.spring.boot.config;

import com.fjn.helper.sql.dialect.conf.SQLInstrumentConfig;
import com.fjn.helper.sql.mybatis.MybatisPagingPluginWrapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "sqlhelper.mybatis")
public class SqlHelperMybatisProperties {

    @NestedConfigurationProperty
    private SQLInstrumentConfig instrument = new SQLInstrumentConfig();
    @NestedConfigurationProperty
    private MybatisPagingPluginWrapper.PluginGlobalConfig pagination = new MybatisPagingPluginWrapper.PluginGlobalConfig();

    public SQLInstrumentConfig getInstrument() {
        return instrument;
    }

    public void setInstrument(SQLInstrumentConfig instrument) {
        this.instrument = instrument;
    }

    public MybatisPagingPluginWrapper.PluginGlobalConfig getPagination() {
        return pagination;
    }

    public void setPagination(MybatisPagingPluginWrapper.PluginGlobalConfig pagination) {
        this.pagination = pagination;
    }
}
