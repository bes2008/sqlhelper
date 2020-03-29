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

package com.jn.sqlhelper.mybatis.spring.boot.autoconfigure;

import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.instrument.SQLInstrumentorConfig;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import com.jn.sqlhelper.mybatis.plugins.CustomScriptLanguageDriver;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import com.jn.sqlhelper.mybatis.SqlHelperMybatisProperties;
import com.jn.sqlhelper.mybatis.plugins.pagination.PaginationConfig;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
@AutoConfigureBefore(MybatisAutoConfiguration.class)
public class SqlHelperMybatisAutoConfiguration implements ConfigurationCustomizer {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisAutoConfiguration.class);

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        return MybatisUtils.vendorDatabaseIdProvider();
    }

    @Bean
    @ConfigurationProperties(prefix = "sqlhelper.mybatis.instrumentor")
    public SQLInstrumentorConfig sqlInstrumentConfig() {
        return new SQLInstrumentorConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "sqlhelper.mybatis.pagination")
    public PaginationConfig paginationPluginConfig() {
        return new PaginationConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "sqlhelper.mybatis")
    public SqlHelperMybatisProperties sqlHelperMybatisProperties(SQLInstrumentorConfig sqlInstrumentConfig, PaginationConfig paginationPluginConfig) {
        SqlHelperMybatisProperties p = new SqlHelperMybatisProperties();
        p.setInstrumentor(sqlInstrumentConfig);
        p.setPagination(paginationPluginConfig);
        return p;
    }

    private SqlHelperMybatisProperties sqlHelperMybatisProperties;

    @Autowired
    public void setSqlHelperMybatisProperties(SqlHelperMybatisProperties sqlHelperMybatisProperties) {
        this.sqlHelperMybatisProperties = sqlHelperMybatisProperties;
    }

    @Override
    public void customize(Configuration configuration) {
        logger.info("Start to customize mybatis configuration with mybatis-spring-boot-autoconfigure");
        configuration.setDefaultScriptingLanguage(CustomScriptLanguageDriver.class);

        SqlHelperMybatisPlugin plugin = new SqlHelperMybatisPlugin();
        plugin.setPaginationConfig(sqlHelperMybatisProperties.getPagination());
        plugin.setInstrumentorConfig(sqlHelperMybatisProperties.getInstrumentor());
        plugin.init();

        logger.info("Add interceptor {} to mybatis configuration", plugin);
        logger.info("The properties of the mybatis plugin [{}] is: {}", Reflects.getFQNClassName(SqlHelperMybatisPlugin.class), sqlHelperMybatisProperties);
        configuration.addInterceptor(plugin);
    }


}
