
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

import com.jn.sqlhelper.dialect.RowSelection;
import com.jn.sqlhelper.dialect.SqlRequestContext;

public class PagingRequestContext<E, R> extends SqlRequestContext {
    private PagingRequest<E, R> request;
    private RowSelection rowSelection;

    public PagingRequestContext() {
        super();
    }

    public PagingRequest<E, R> getRequest() {
        return this.request;
    }

    public void setRequest(PagingRequest<E, R> request) {
        this.request = request;
        this.request.setCtx(this);
    }

    public RowSelection getRowSelection() {
        return this.rowSelection;
    }

    public void setRowSelection(RowSelection rowSelection) {
        this.rowSelection = rowSelection;
    }

    public static final String BEFORE_SUBQUERY_PARAMETERS_COUNT = "BEFORE_SUBQUERY_PARAMETERS_COUNT";
    public static final String AFTER_SUBQUERY_PARAMETERS_COUNT = "AFTER_SUBQUERY_PARAMETERS_COUNT";
}
