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
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.sqlhelper.datasource.definition.DataSourcesProperties;
import com.jn.sqlhelper.datasource.key.DataSourceKey;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import com.jn.sqlhelper.datasource.key.router.DataSourceWeighter;
import com.jn.sqlhelper.datasource.key.router.RandomRouter;
import com.jn.sqlhelper.datasource.key.router.RoundRobinRouter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "sqlhelper.dynamicDataSource.enabled", havingValue = "true", matchIfMissing = false)
@Configuration
@AutoConfigureAfter(DynamicDataSourcesAutoConfiguration.class)
public class DynamicDataSourceLoadBalanceRoutersAutoconfiguration {
    @Bean
    @ConditionalOnMissingBean
    public DataSourceWeighter dataSourceRoundRobinWeighter() {
        return new DataSourceWeighter() {
            @Override
            public int getWeight(DataSourceKey key, MethodInvocation methodInvocation) {
                return 0;
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RoundRobinRouter roundRobinRouter(
            DataSourceKeySelector selector,
            @Qualifier("dataSourceRandomWeighter") DataSourceWeighter weighter,
            DataSourcesProperties dataSourcesProperties) {
        RoundRobinRouter router = new RoundRobinRouter();
        router.setWeighter(weighter);

        boolean isDefault = false;
        if (Emptys.isNotEmpty(dataSourcesProperties.getDefaultRouter()) && Objs.equals(dataSourcesProperties.getDefaultRouter(), router.getName())) {
            isDefault = true;
        }
        selector.registerRouter(router, isDefault);
        return router;

    }

    @Bean
    @ConditionalOnMissingBean
    public DataSourceWeighter dataSourceRandomWeighter() {
        return new DataSourceWeighter() {
            @Override
            public int getWeight(DataSourceKey key, MethodInvocation methodInvocation) {
                return 0;
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RandomRouter dataSourceRandomRouter(
            DataSourceKeySelector selector,
            @Qualifier("dataSourceRandomWeighter") DataSourceWeighter weighter,
            DataSourcesProperties dataSourcesProperties) {
        RandomRouter router = new RandomRouter();
        router.setWeighter(weighter);

        boolean isDefault = false;
        if (Emptys.isNotEmpty(dataSourcesProperties.getDefaultRouter()) && Objs.equals(dataSourcesProperties.getDefaultRouter(), router.getName())) {
            isDefault = true;
        }
        selector.registerRouter(router, isDefault);
        return router;
    }
}
