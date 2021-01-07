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

package com.jn.sqlhelper.datasource.spring.boot;

import com.jn.langx.invocation.MethodInvocation;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.router.DataSourceWeighter;
import com.jn.sqlhelper.datasource.key.router.RandomRouter;
import com.jn.sqlhelper.datasource.key.router.RoundRobinRouter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 3.4.1
 */
@ConditionalOnProperty(name = "sqlhelper.dynamicDataSource.enabled", havingValue = "true", matchIfMissing = false)
@Configuration
@AutoConfigureAfter(DynamicDataSourcesAutoConfiguration.class)
public class DynamicDataSourceLoadBalanceAutoConfiguration {
    @Bean(name = "dataSourceRoundRobinWeighter")
    @ConditionalOnMissingBean(name = "dataSourceRoundRobinWeighter")
    public DataSourceWeighter dataSourceRoundRobinWeighter() {
        return new DataSourceWeighter() {
            @Override
            public int getWeight(DataSourceKey key, MethodInvocation methodInvocation) {
                return 0;
            }
        };
    }

    @Bean(name="roundRobinRouter")
    @ConditionalOnMissingBean(name="roundRobinRouter")
    public RoundRobinRouter roundRobinRouter(
            @Qualifier("dataSourceRoundRobinWeighter") DataSourceWeighter weighter) {
        RoundRobinRouter router = new RoundRobinRouter();
        router.setWeighter(weighter);
        return router;

    }

    @Bean(name = "dataSourceRandomWeighter")
    @ConditionalOnMissingBean(name = "dataSourceRandomWeighter")
    public DataSourceWeighter dataSourceRandomWeighter() {
        return new DataSourceWeighter() {
            @Override
            public int getWeight(DataSourceKey key, MethodInvocation methodInvocation) {
                return 0;
            }
        };
    }

    @Bean("dataSourceRandomRouter")
    @ConditionalOnMissingBean(name="dataSourceRandomRouter")
    public RandomRouter dataSourceRandomRouter(
            @Qualifier("dataSourceRandomWeighter") DataSourceWeighter weighter) {
        RandomRouter router = new RandomRouter();
        router.setWeighter(weighter);
        return router;
    }
}
