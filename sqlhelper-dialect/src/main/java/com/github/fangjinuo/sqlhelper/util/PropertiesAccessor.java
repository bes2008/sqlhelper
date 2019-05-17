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

package com.github.fangjinuo.sqlhelper.util;

import java.util.Properties;

public class PropertiesAccessor {
    private Properties props;

    public PropertiesAccessor(Properties properties) {
        this.props = properties;
    }

    public String getString(String key) {
        return props.getProperty(key, "0");
    }

    public String getString(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public Integer getInteger(String key) {
        return getInteger(key, 0);
    }

    public Integer getInteger(String key, int defaultValue) {
        return Integer.parseInt(props.getProperty(key, "" + defaultValue));
    }

    public Short getShort(String key) {
        return getShort(key, new Short("" + 0));
    }

    public Short getShort(String key, short defaultValue) {
        return Short.parseShort(props.getProperty(key, "" + defaultValue));
    }

    public Double getDouble(String key) {
        return getDouble(key, 0.0d);
    }

    public Double getDouble(String key, double defaultValue) {
        return Double.parseDouble(props.getProperty(key, "" + defaultValue));
    }

    public Float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    public Float getFloat(String key, float defaultValue) {
        return Float.parseFloat(props.getProperty(key, "" + defaultValue));
    }

    public Long getLong(String key) {
        return getLong(key, "0");
    }

    public Long getLong(String key, String defaultValue) {
        return Long.parseLong(props.getProperty(key, "" + defaultValue));
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(props.getProperty(key, "" + defaultValue));
    }
}
