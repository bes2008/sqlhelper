/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.datasource;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Strings;
import com.jn.langx.util.hash.HashCodeBuilder;

public class DataSourceKey {
    @NonNull
    private String group;
    @NonNull
    private String name;

    public DataSourceKey(){}

    public DataSourceKey(String group, String name) {
        this.group = group;
        this.name = name;
    }

    public boolean isAvailable(){
        return Strings.isNotBlank(group) && Strings.isNotBlank(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSourceKey that = (DataSourceKey) o;
        return group.equals(that.group) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().with(group).with(name).build();
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
