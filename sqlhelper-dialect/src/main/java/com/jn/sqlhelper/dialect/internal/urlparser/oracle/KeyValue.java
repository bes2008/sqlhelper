
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

package com.jn.sqlhelper.dialect.internal.urlparser.oracle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeyValue {
    public String key;
    public String value;
    public List<KeyValue> keyValueList;

    public String getKey() {
        return this.key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public List<KeyValue> getKeyValueList() {
        if (this.keyValueList == null) {
            return Collections.emptyList();
        }
        return this.keyValueList;
    }

    public void addKeyValueList(final KeyValue keyValue) {
        if (this.keyValueList == null) {
            this.keyValueList = new ArrayList<KeyValue>();
        }
        this.keyValueList.add(keyValue);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{key='").append(this.key).append('\'');
        if (this.value != null) {
            sb.append(", value='").append(this.value).append('\'');
        }
        if (this.keyValueList != null) {
            sb.append(", keyValueList=").append(this.keyValueList);
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final KeyValue keyValue = (KeyValue) o;
        Label_0062:
        {
            if (this.key != null) {
                if (this.key.equals(keyValue.key)) {
                    break Label_0062;
                }
            } else if (keyValue.key == null) {
                break Label_0062;
            }
            return false;
        }
        Label_0097:
        {
            if (this.keyValueList != null) {
                if (this.keyValueList.equals(keyValue.keyValueList)) {
                    break Label_0097;
                }
            } else if (keyValue.keyValueList == null) {
                break Label_0097;
            }
            return false;
        }
        if (this.value != null) {
            if (this.value.equals(keyValue.value)) {
                return true;
            }
        } else if (keyValue.value == null) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = (this.key != null) ? this.key.hashCode() : 0;
        result = 31 * result + ((this.value != null) ? this.value.hashCode() : 0);
        result = 31 * result + ((this.keyValueList != null) ? this.keyValueList.hashCode() : 0);
        return result;
    }
}
