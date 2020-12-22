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

package com.jn.sqlhelper.mybatis.spring.boot.autoconfigure;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.mybatis.spring.datasource.DelegatingSqlSessionFactory;
import com.jn.sqlhelper.mybatis.spring.datasource.DynamicSqlSessionFactory;
import com.jn.sqlhelper.mybatis.spring.datasource.DynamicSqlSessionTemplate;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import javax.sql.DataSource;
import java.util.List;

@EnableConfigurationProperties(MybatisProperties.class)
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@AutoConfigureBefore({MybatisAutoConfiguration.class, DataSourceAutoConfiguration.class})
@ConditionalOnBean(name = "dataSourcesFactoryBean")
@Configuration
public class DynamicSqlSessionTemplateAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DynamicSqlSessionTemplateAutoConfiguration.class);

    @Bean("sqlSessionFactory")
    public DynamicSqlSessionFactory dynamicSqlSessionFactory(
            final ObjectProvider<DataSourceRegistry> registryProvider,
            @Qualifier("dataSourcesFactoryBean")
                    ListFactoryBean dataSourcesFactoryBean,
            final MybatisProperties properties,
            final ObjectProvider<Interceptor[]> interceptorsProvider,
            final ResourceLoader resourceLoader,
            final ObjectProvider<DatabaseIdProvider> databaseIdProvider,
            final ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) throws BeanCreationException {
        List<DataSource> dataSources = null;
        try {
            List ds = dataSourcesFactoryBean.getObject();
            dataSources = (List<DataSource>) ds;
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (Emptys.isNotEmpty(dataSources)) {
            try {
                registryProvider.getObject();
            } catch (BeansException ex) {
                logger.error("please check whether the sqlhelper-datasource.jar in the classpath or not");
                throw ex;
            }
            final DynamicSqlSessionFactory dynamicSqlSessionFactory = new DynamicSqlSessionFactory();
            Collects.forEach(dataSources, new Consumer<DataSource>() {
                @Override
                public void accept(DataSource dataSource) {
                    NamedDataSource namedDataSource = registryProvider.getObject().wrap(dataSource);
                    try {
                        SqlSessionFactory delegate = createSqlSessionFactory(dataSource, properties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
                        if (delegate != null) {
                            DelegatingSqlSessionFactory sqlSessionFactory = new DelegatingSqlSessionFactory();
                            sqlSessionFactory.setDelegate(delegate);
                            PersistenceExceptionTranslator translator = new MyBatisExceptionTranslator(delegate.getConfiguration().getEnvironment().getDataSource(), true);
                            sqlSessionFactory.setPersistenceExceptionTranslator(translator);
                            dynamicSqlSessionFactory.addSqlSessionFactory(namedDataSource.getDataSourceKey(), sqlSessionFactory);
                        }
                    } catch (Throwable ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            });
            return dynamicSqlSessionFactory;
        } else {
            throw new BeanCreationException("Can't find any jdbc datasource");
        }

    }

    private SqlSessionFactory createSqlSessionFactory(DataSource dataSource,
                                                      MybatisProperties properties,
                                                      ObjectProvider<Interceptor[]> interceptorsProvider,
                                                      ResourceLoader resourceLoader,
                                                      ObjectProvider<DatabaseIdProvider> databaseIdProviderObjectProvider,
                                                      ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) throws Exception {
        MybatisAutoConfiguration mybatisAutoConfiguration = new MybatisAutoConfiguration(properties, interceptorsProvider, resourceLoader, databaseIdProviderObjectProvider, configurationCustomizersProvider);
        mybatisAutoConfiguration.afterPropertiesSet();
        return mybatisAutoConfiguration.sqlSessionFactory(dataSource);
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(MybatisProperties mybatisProperties, SqlSessionFactory sessionFactory) {
        DynamicSqlSessionTemplate template = new DynamicSqlSessionTemplate(sessionFactory, mybatisProperties.getExecutorType());
        return template;
    }
}
