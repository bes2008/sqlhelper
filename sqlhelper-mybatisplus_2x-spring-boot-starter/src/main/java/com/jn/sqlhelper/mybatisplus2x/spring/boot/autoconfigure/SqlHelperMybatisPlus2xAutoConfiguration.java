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

package com.jn.sqlhelper.mybatisplus2x.spring.boot.autoconfigure;


import com.baomidou.mybatisplus.spring.boot.starter.ConfigurationCustomizer;
import com.baomidou.mybatisplus.spring.boot.starter.MybatisPlusAutoConfiguration;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import com.jn.sqlhelper.mybatisplus2x.plugins.pagination.CustomMybatisPlus2xScriptLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@org.springframework.context.annotation.Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore({MybatisPlusAutoConfiguration.class})
public class SqlHelperMybatisPlus2xAutoConfiguration implements ConfigurationCustomizer {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperMybatisPlus2xAutoConfiguration.class);

    private SqlHelperMybatisPlugin plugin;

    @Override
    public void customize(Configuration configuration) {
        logger.info("===[SQLHelper & MyBatis-Plus 2.x]=== Start to customize mybatis-plus 2.x configuration with sqlhelper-mybatisplus_2x-spring-boot-starter,mybatis-plus-boot-starter");
        configuration.setDefaultScriptingLanguage(CustomMybatisPlus2xScriptLanguageDriver.class);
        configuration.addInterceptor(plugin);
        logger.info("===[SQLHelper & MyBatis-Plus 2.x]=== Add interceptor {} to mybatis-plus 2.x configuration", plugin);
    }

    @Autowired
    public void setPlugin(SqlHelperMybatisPlugin plugin) {
        this.plugin = plugin;
    }
}