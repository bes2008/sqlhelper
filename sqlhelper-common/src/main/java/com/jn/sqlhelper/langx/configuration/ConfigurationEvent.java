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

package com.jn.sqlhelper.langx.configuration;

import com.jn.langx.event.DomainEvent;
import com.jn.langx.text.StringTemplates;

public class ConfigurationEvent<T extends Configuration> extends DomainEvent<T> {
    private ConfigurationEventType eventType;

    public ConfigurationEvent(String eventDomain, T t) {
        super(eventDomain, t);
    }

    public ConfigurationEventType getEventType() {
        return eventType;
    }

    public void setEventType(ConfigurationEventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return StringTemplates.format("{domain: {0}, eventType:{1}, source: {2}}", getDomain(), eventType.name(), getSource().toString());
    }
}
