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

package com.jn.sqlhelper.mybatis.spring.boot.autoconfigure;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.TransactionFactory;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import javax.sql.DataSource;

public class DynamicDataSourceTransactionCustomizer implements ConfigurationCustomizer {
    @Override
    public void customize(Configuration configuration) {
        Environment old = configuration.getEnvironment();

        TransactionFactory transactionFactory = old.getTransactionFactory();
        if (transactionFactory instanceof SpringManagedTransactionFactory) {

        }

        String id = old.getId();
        DataSource dataSource = old.getDataSource();
        Environment environment = configuration.getEnvironment();
        configuration.setEnvironment(environment);
    }
}
