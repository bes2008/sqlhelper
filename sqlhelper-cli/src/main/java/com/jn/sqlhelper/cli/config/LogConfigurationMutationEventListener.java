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

package com.jn.sqlhelper.cli.config;

import com.jn.langx.event.EventListener;
import com.jn.langx.configuration.Configuration;
import com.jn.langx.configuration.ConfigurationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogConfigurationMutationEventListener implements EventListener<ConfigurationEvent<Configuration>> {
    private static final Logger logger = LoggerFactory.getLogger(LogConfigurationMutationEventListener.class);

    @Override
    public void on(ConfigurationEvent<Configuration> event) {
        logger.info(event.toString());
    }
}
