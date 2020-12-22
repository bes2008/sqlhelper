/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.tkmapper.spring.boot.autoconfigure;

import com.jn.sqlhelper.mybatis.plugins.CustomScriptLanguageDriver;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import tk.mybatis.mapper.autoconfigure.ConfigurationCustomizer;
import tk.mybatis.mapper.autoconfigure.MapperAutoConfiguration;

@org.springframework.context.annotation.Configuration
@AutoConfigureBefore(MapperAutoConfiguration.class)
public class SqlHelperTkMapperAutoConfiguration implements ConfigurationCustomizer {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperTkMapperAutoConfiguration.class);

    private SqlHelperMybatisPlugin plugin;

    @Override
    public void customize(Configuration configuration) {
        logger.info("===[SQLHelper & tk.mapper]=== Start to customize mybatis configuration with sqlhelper-tkmapper-spring-boot-starter,tk.mybatis:mapper-spring-boot-starter");
        configuration.setDefaultScriptingLanguage(CustomScriptLanguageDriver.class);
        configuration.addInterceptor(plugin);
        logger.info("===[SQLHelper & tk.mapper]=== Add interceptor {} to tk.mapper configuration", plugin);
    }

    @Autowired
    public void setPlugin(SqlHelperMybatisPlugin plugin) {
        this.plugin = plugin;
    }
}
