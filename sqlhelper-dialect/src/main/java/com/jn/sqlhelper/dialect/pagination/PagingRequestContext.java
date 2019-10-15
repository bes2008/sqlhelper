
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

package com.jn.sqlhelper.dialect.pagination;

import com.jn.langx.util.BasedStringAccessor;
import com.jn.sqlhelper.dialect.RowSelection;

import java.util.HashMap;
import java.util.Map;

public class PagingRequestContext<E, R> extends BasedStringAccessor<String, Map<String, Object>> {
    private PagingRequest<E, R> request;
    private RowSelection rowSelection;

    public PagingRequestContext() {
        setTarget(new HashMap<String, Object>());
    }

    public PagingRequest<E, R> getRequest() {
        return this.request;
    }

    public void setRequest(PagingRequest<E, R> request) {
        this.request = request;
    }

    public RowSelection getRowSelection() {
        return this.rowSelection;
    }

    public void setRowSelection(RowSelection rowSelection) {
        this.rowSelection = rowSelection;
    }

    @Override
    public Object get(String key) {
        return getTarget().get(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        Object value = getTarget().get(key);
        return value == null ? defaultValue : value.toString();
    }

    @Override
    public void set(String key, Object value) {
        getTarget().put(key, value);
    }
}
