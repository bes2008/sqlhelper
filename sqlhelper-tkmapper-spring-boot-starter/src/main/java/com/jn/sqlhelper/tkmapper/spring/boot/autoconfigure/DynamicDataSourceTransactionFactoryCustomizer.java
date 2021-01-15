/*
 * Copyright 2021 the original author or authors.
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

package com.jn.sqlhelper.tkmapper.spring.boot.autoconfigure;

import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.datasource.spring.boot.DynamicTransactionAutoConfiguration;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicDataSourceManagedTransactionFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.TransactionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tk.mybatis.mapper.autoconfigure.ConfigurationCustomizer;
import tk.mybatis.mapper.autoconfigure.MapperAutoConfiguration;

import javax.sql.DataSource;


@org.springframework.context.annotation.Configuration
@ConditionalOnProperty(name = "sqlhelper.dynamic-datasource.enabled", havingValue = "true", matchIfMissing = false)
@ConditionalOnBean(name = "dynamicDataSourceTransactionAdvisor")
@AutoConfigureBefore(MapperAutoConfiguration.class)
@AutoConfigureAfter(DynamicTransactionAutoConfiguration.class)
public class DynamicDataSourceTransactionFactoryCustomizer implements ConfigurationCustomizer {
    @Override
    public void customize(Configuration configuration) {
        Environment oldEnv = configuration.getEnvironment();
        if (oldEnv != null) {
            String id = oldEnv.getId();
            DataSource dataSource = oldEnv.getDataSource();
            if (dataSource instanceof NamedDataSource) {
                id = ((NamedDataSource) dataSource).getDataSourceKey().getId();
            }

            TransactionFactory transactionFactory = new DynamicDataSourceManagedTransactionFactory();
            Environment newEnv = new Environment(id, transactionFactory, dataSource);
            configuration.setEnvironment(newEnv);
        }
    }
}
