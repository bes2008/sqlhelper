/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.mybatis.springboot.autoconfigure;

import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.instrument.SQLInstrumentorConfig;
import com.jn.sqlhelper.mybatis.SqlHelperMybatisProperties;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import com.jn.sqlhelper.mybatis.plugins.pagination.PaginationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqlHelperMybatisPluginAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisPluginAutoConfiguration.class);

    @Bean
    @ConfigurationProperties(prefix = "sqlhelper.mybatis")
    public SqlHelperMybatisProperties sqlHelperMybatisProperties() {
        SqlHelperMybatisProperties p = new SqlHelperMybatisProperties();
        SQLInstrumentorConfig config = p.getInstrumentor();
        config.setName("mybatis");
        return p;
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlHelperMybatisPlugin sqlHelperMybatisPlugin(SqlHelperMybatisProperties sqlHelperMybatisProperties){
        SqlHelperMybatisPlugin plugin = new SqlHelperMybatisPlugin();
        plugin.setPaginationConfig(sqlHelperMybatisProperties.getPagination());
        plugin.setInstrumentorConfig(sqlHelperMybatisProperties.getInstrumentor());
        plugin.init();
        logger.info("The properties of the mybatis plugin [{}] is: {}", Reflects.getFQNClassName(SqlHelperMybatisPlugin.class), sqlHelperMybatisProperties);
        return plugin;
    }


}
