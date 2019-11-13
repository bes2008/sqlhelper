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

package com.jn.sqlhelper.common.connection;

import com.jn.langx.configuration.ConfigurationSerializer;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

public class PropertiesNamedConnectionConfigurationSerializer implements ConfigurationSerializer<NamedConnectionConfiguration, String> {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesNamedConnectionConfigurationSerializer.class);

    @Override
    public String serialize(NamedConnectionConfiguration configuration) {
        final Properties props = new Properties();
        props.setProperty(ConnectionConfiguration.DRIVER, configuration.getDriver());
        props.setProperty(ConnectionConfiguration.URL, configuration.getUrl());
        props.setProperty(ConnectionConfiguration.USER, configuration.getUser());
        props.setProperty(ConnectionConfiguration.PASSWORD, configuration.getPassword());
        Collects.forEach(configuration.getDriverProps(), new Consumer2<Object, Object>() {
            @Override
            public void accept(Object key, Object value) {
                props.setProperty(key.toString(), value.toString());
            }
        });

        StringWriter stringWriter = new StringWriter();
        String comment = StringTemplates.formatWithPlaceholder("JDBC Connection Configuration [{}]", configuration.getId());
        try {
            props.store(stringWriter, comment);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return stringWriter.toString();
    }
}
