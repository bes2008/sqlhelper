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

import com.jn.sqlhelper.mybatis.plugins.CustomScriptLanguageDriver;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;

@org.springframework.context.annotation.Configuration
@AutoConfigureAfter(SqlHelperMybatisPluginAutoConfiguration.class)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
public class SqlHelperMybatisAutoConfiguration implements ConfigurationCustomizer {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisAutoConfiguration.class);

    @Autowired
    private SqlHelperMybatisPlugin plugin;

    @Override
    public void customize(Configuration configuration) {
        logger.info("Start to customize mybatis configuration with mybatis-spring-boot-autoconfigure");
        configuration.setDefaultScriptingLanguage(CustomScriptLanguageDriver.class);
        logger.info("Add interceptor {} to mybatis configuration", plugin);
        configuration.addInterceptor(plugin);
    }


}
