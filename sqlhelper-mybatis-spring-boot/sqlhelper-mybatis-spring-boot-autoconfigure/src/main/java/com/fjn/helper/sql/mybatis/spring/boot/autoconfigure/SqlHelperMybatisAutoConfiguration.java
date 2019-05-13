/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fjn.helper.sql.mybatis.spring.boot.autoconfigure;

import com.fjn.helper.sql.mybatis.plugins.pagination.MybatisPagingPluginWrapper;
import com.fjn.helper.sql.mybatis.MybatisUtils;
import com.fjn.helper.sql.mybatis.plugins.pagination.CustomScriptLanguageDriver;
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
public class SqlHelperMybatisAutoConfiguration implements ConfigurationCustomizer {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisAutoConfiguration.class);

    private MybatisPagingPluginWrapper mybatisPagingPluginWrapper;

    @Autowired
    public void setMybatisPagingPluginWrapper(MybatisPagingPluginWrapper mybatisPagingPluginWrapper) {
        this.mybatisPagingPluginWrapper = mybatisPagingPluginWrapper;
    }

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
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
        configuration.setDefaultScriptingLanguage(CustomScriptLanguageDriver.class);
        List<Interceptor> sqlhelperPlugins = mybatisPagingPluginWrapper.getPlugins();
        for (Interceptor sqlhelperPlugin : sqlhelperPlugins) {
            logger.info("Add interceptor {} to mybatis configuration", sqlhelperPlugin);
            configuration.addInterceptor(sqlhelperPlugin);
        }
    }
}
