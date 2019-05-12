package com.fjn.helper.examples.mybatis.spring.boot.config;

import com.fjn.helper.sql.mybatis.MybatisPagingPluginWrapper;
import com.fjn.helper.sql.mybatis.MybatisUtils;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(SqlHelperMybatisProperties.class)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
public class SqlHelperMybatisConfigurator implements ConfigurationCustomizer {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisConfigurator.class);

    private MybatisPagingPluginWrapper mybatisPagingPluginWrapper;

    @Autowired
    public void setMybatisPagingPluginWrapper(MybatisPagingPluginWrapper mybatisPagingPluginWrapper) {
        this.mybatisPagingPluginWrapper = mybatisPagingPluginWrapper;
    }

    @Bean
    public DatabaseIdProvider databaseIdProvider(){
        return MybatisUtils.vendorDatabaseIdProvider();
    }

    @Bean
    public MybatisPagingPluginWrapper mybatisPagingPluginWrapper(@Autowired SqlHelperMybatisProperties sqlHelperMybatisProperties) {
        MybatisPagingPluginWrapper wrapper = new MybatisPagingPluginWrapper();
        wrapper.initPlugin(sqlHelperMybatisProperties.getPagination(), sqlHelperMybatisProperties.getInstrumentor());
        return wrapper;
    }

    @Override
    public void customize(Configuration configuration) {
        logger.info("Start to customize mybatis configuration with mybatis-spring-boot-autoconfigure");
        configuration.setDefaultScriptingLanguage(MybatisPagingPluginWrapper.CustomScriptLanguageDriver.class);
        List<Interceptor> sqlhelperPlugins = mybatisPagingPluginWrapper.getPlugins();
        for (Interceptor sqlhelperPlugin : sqlhelperPlugins) {
            configuration.addInterceptor(sqlhelperPlugin);
        }
    }
}
