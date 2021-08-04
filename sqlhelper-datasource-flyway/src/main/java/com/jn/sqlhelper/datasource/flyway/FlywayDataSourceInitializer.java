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

package com.jn.sqlhelper.datasource.flyway;

import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.text.properties.Props;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate2;
import com.jn.sqlhelper.datasource.DataSourceInitializer;
import com.jn.sqlhelper.datasource.NamedDataSource;
import com.jn.sqlhelper.datasource.config.DataSourceProperties;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;


public class FlywayDataSourceInitializer extends AbstractInitializable implements DataSourceInitializer {
    private NamedDataSource dataSource;

    @Override
    public NamedDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void setDataSource(NamedDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void doInit() throws InitializationException {
        Preconditions.checkNotNull(dataSource, "the datasource is null");
        // 找出所有的 flyway. 开头的配置项
        Map<String, String> props = new LinkedHashMap<String, String>();
        DataSourceProperties dataSourceProperties = dataSource.getDataSourceProperties();
        Properties extProps = dataSourceProperties.getExtProps();
        Predicate2<String, String> predicate = new Predicate2<String, String>() {
            @Override
            public boolean test(String key, String value) {
                return Strings.startsWith(key, "flyway.");
            }
        };
        props.putAll(Collects.filter(Props.toStringMap(extProps), predicate));


        ClassicConfiguration configuration = new ClassicConfiguration();
        configuration.setDataSource(dataSource);
        Flyway flyway = new Flyway(configuration);
        flyway.migrate();
    }
}

