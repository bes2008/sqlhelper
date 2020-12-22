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
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import com.jn.sqlhelper.mybatisplus.plugins.pagination.CustomMybatisPlusScriptLanguageDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;

@org.springframework.context.annotation.Configuration
@AutoConfigureBefore(MybatisPlusAutoConfiguration.class)
public class SqlHelperMybatisPlusAutoConfiguration implements ConfigurationCustomizer {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisPlusAutoConfiguration.class);
    private SqlHelperMybatisPlugin plugin;

    @Override
    public void customize(MybatisConfiguration configuration) {
        logger.info("===[SQLHelper & MyBatis-Plus 3.x]=== Start to customize mybatis-plus configuration with sqlhelper-mybatisplus-spring-boot-starter, mybatis-plus-boot-starter");
        configuration.setDefaultScriptingLanguage(CustomMybatisPlusScriptLanguageDriver.class);
        configuration.addInterceptor(plugin);
        logger.info("===[SQLHelper & MyBatis-Plus 3.x]=== Add interceptor {} to mybatis-plus 3.x configuration", plugin);
    }

    @Autowired
    public void setPlugin(SqlHelperMybatisPlugin plugin) {
        this.plugin = plugin;
    }

}