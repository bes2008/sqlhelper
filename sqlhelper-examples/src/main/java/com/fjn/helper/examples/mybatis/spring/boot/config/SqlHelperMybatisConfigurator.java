package com.fjn.helper.examples.mybatis.spring.boot.config;

import com.fjn.helper.sql.mybatis.MyBatisPagingPluginWrapper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(SqlHelperMybatisProperties.class)
public class SqlHelperMybatisConfigurator implements ConfigurationCustomizer {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisConfigurator.class);

    private MyBatisPagingPluginWrapper mybatisPagingPluginWrapper;

    @Autowired
    public void setMybatisPagingPluginWrapper(MyBatisPagingPluginWrapper mybatisPagingPluginWrapper) {
        this.mybatisPagingPluginWrapper = mybatisPagingPluginWrapper;
    }

    @Bean
    public MyBatisPagingPluginWrapper mybatisPagingPluginWrapper(@Autowired SqlHelperMybatisProperties sqlHelperMybatisProperties) {
        MyBatisPagingPluginWrapper wrapper = new MyBatisPagingPluginWrapper();
        wrapper.initPlugin(sqlHelperMybatisProperties.getPagination(), sqlHelperMybatisProperties.getInstrument());
        return wrapper;
    }

    @Override
    public void customize(Configuration configuration) {
        logger.info("Start to customize mybatis configuration with mybatis-spring-boot-autoconfigure");
        configuration.setDefaultScriptingLanguage(MyBatisPagingPluginWrapper.CustomScriptLanguageDriver.class);
        List<Interceptor> sqlhelperPlugins = mybatisPagingPluginWrapper.getPlugins();
        for (Interceptor sqlhelperPlugin : sqlhelperPlugins) {
            configuration.addInterceptor(sqlhelperPlugin);
        }
    }
}
