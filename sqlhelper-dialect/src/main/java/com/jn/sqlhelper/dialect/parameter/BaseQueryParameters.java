
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

package com.jn.sqlhelper.dialect.parameter;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.dialect.QueryParameters;
import com.jn.sqlhelper.dialect.RowSelection;

import java.util.Map;

public class BaseQueryParameters<P> implements QueryParameters<P> {
    private RowSelection rowSelection;
    private boolean isCallable;
    protected P parameters;
    private int beforeSubqueryCount = 0;
    private int afterSubqueryCount = 0;
    protected P beforeSubqueryParameters;
    protected P subqueryParameters;
    protected P afterSubqueryParameters;

    @Override
    public P getParameterValues() {
        return parameters;
    }

    @Override
    public int getParameterValuesSize() {
        if (parameters == null) {
            return 0;
        }
        if (parameters instanceof Map) {
            return ((Map) parameters).size();
        }
        return Collects.count(parameters);
    }

    @Override
    public RowSelection getRowSelection() {
        return rowSelection;
    }

    public void setRowSelection(RowSelection rowSelection) {
        this.rowSelection = rowSelection;
    }

    @Override
    public boolean isCallable() {
        return isCallable;
    }

    public void setCallable(boolean callable) {
        isCallable = callable;
    }

    /**
     * invoke it when a paging request
     */
    public void setParameters(P parameters) {
        this.parameters = parameters;
    }

    /**
     * invoke it when a subquery  paging request
     */
    public void setParameters(P parameters, int beforeSubqueryCount, int afterSubqueryCount) {
        this.parameters = parameters;
        this.beforeSubqueryCount = beforeSubqueryCount;
        this.afterSubqueryCount = afterSubqueryCount;
    }

    @Override
    public int getBeforeSubqueryParameterCount() {
        return beforeSubqueryCount;
    }

    @Override
    public int getAfterSubqueryParameterCount() {
        return afterSubqueryCount;
    }

    @Override
    public P getBeforeSubqueryParameterValues() {
        return beforeSubqueryParameters;
    }

    @Override
    public P getAfterSubqueryParameterValues() {
        return afterSubqueryParameters;
    }

    @Override
    public P getSubqueryParameterValues() {
        return subqueryParameters;
    }
}