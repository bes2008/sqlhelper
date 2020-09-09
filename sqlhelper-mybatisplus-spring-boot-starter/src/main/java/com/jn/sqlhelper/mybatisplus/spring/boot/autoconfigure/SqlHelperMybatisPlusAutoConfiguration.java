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

package com.jn.sqlhelper.mybatisplus.spring.boot.autoconfigure;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.instrument.SQLInstrumentorConfig;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import com.jn.sqlhelper.mybatis.SqlHelperMybatisProperties;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import com.jn.sqlhelper.mybatisplus.plugins.pagination.CustomMybatisPlusScriptLanguageDriver;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
@AutoConfigureBefore(MybatisPlusAutoConfiguration.class)
public class SqlHelperMybatisPlusAutoConfiguration implements ConfigurationCustomizer {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisPlusAutoConfiguration.class);

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        return MybatisUtils.vendorDatabaseIdProvider();
    }

    @Bean
    @ConfigurationProperties(prefix = "sqlhelper.mybatis")
    public SqlHelperMybatisProperties sqlHelperMybatisProperties() {
        SqlHelperMybatisProperties properties = new SqlHelperMybatisProperties();
        SQLInstrumentorConfig config = properties.getInstrumentor();
        config.setName("mybatisplus");
        return properties;
    }

    private SqlHelperMybatisProperties sqlHelperMybatisProperties;

    @Autowired
    public void setSqlHelperMybatisPlusProperties(SqlHelperMybatisProperties sqlHelperMybatisProperties) {
        this.sqlHelperMybatisProperties = sqlHelperMybatisProperties;
    }

    @Override
    public void customize(MybatisConfiguration configuration) {
        logger.info("Start to customize mybatis-plus configuration with mybatis-plus-boot-starter");
        configuration.setDefaultScriptingLanguage(CustomMybatisPlusScriptLanguageDriver.class);

        SqlHelperMybatisPlugin plugin = new SqlHelperMybatisPlugin();
        plugin.setPaginationConfig(sqlHelperMybatisProperties.getPagination());
        plugin.setInstrumentorConfig(sqlHelperMybatisProperties.getInstrumentor());
        plugin.init();

        logger.info("Add interceptor {} to mybatis-plus configuration", plugin);
        logger.info("The properties of the mybatis-plus plugin [{}] is: {}", Reflects.getFQNClassName(SqlHelperMybatisPlugin.class), sqlHelperMybatisProperties);
        configuration.addInterceptor(plugin);
    }


}