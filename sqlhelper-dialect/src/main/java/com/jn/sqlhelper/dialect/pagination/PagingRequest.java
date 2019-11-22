
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

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.easyjson.core.annotation.Ignore;
import com.jn.easyjson.core.exclusion.IgnoreAnnotationExclusion;
import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.dialect.orderby.OrderBy;

import java.io.Serializable;

public class PagingRequest<C, E> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Boolean count = null;
    private String countColumn;
    private String dialect;
    // begin 1
    private int pageNo = 1;
    // pageSize < 0, the limit is Integer.MAX
    // pageSize =0, is Empty paging request, the limit is 0
    // pageSize > 0, the limit is pageSize
    private int pageSize;
    private Integer fetchSize;
    private int maxRows = -1;
    private int timeout;
    private OrderBy orderBy;
    private Boolean useLastPageIfPageNoOut;
    private C condition;
    private PagingResult<E> result;

    private boolean isSubQueryPaging = false;
    private String subqueryPagingStartFlag;
    private String subqueryPagingEndFlag;

    @Ignore
    private PagingRequestContext ctx;

    /**
     * Nothing to do, will not do query, the result is empty list
     */
    public boolean isEmptyRequest() {
        return this.pageSize == 0;
    }

    /**
     * Get all matched records with out paging limit
     */
    public boolean isGetAllRequest() {
        return this.pageSize < 0 && pageNo == 1;
    }

    public boolean isGetAllFromNonZeroOffsetRequest() {
        return this.pageSize < 0 && pageNo > 1;
    }

    public boolean isValidRequest() {
        return this.pageSize > 0 || isGetAllFromNonZeroOffsetRequest();
    }

    public PagingRequest<C, E> limit(int pageNo, int pageSize) {
        return this.setPageNo(pageNo).setPageSize(pageSize);
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public PagingRequest<C, E> setPageNo(int pageNo) {
        if (pageNo <= 0) {
            return this;
        }
        this.pageNo = pageNo;
        return this;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public PagingRequest<C, E> setPageSize(int pageSize) {
        if (pageSize < 0) {
            this.pageSize = -1;
            return this;
        }
        this.pageSize = pageSize;
        return this;
    }

    public Integer getFetchSize() {
        return this.fetchSize;
    }

    public PagingRequest setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public PagingRequest<C, E> setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getOrderByAsString() {
        return this.orderBy.toString();
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public PagingRequest<C, E> setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public boolean needOrderBy() {
        if (this.orderBy == null || !this.orderBy.isValid()) {
            return false;
        }
        return true;
    }

    public C getCondition() {
        return (C) this.condition;
    }

    public PagingRequest<C, E> setCondition(C condition) {
        this.condition = condition;
        return this;
    }

    public Boolean needCount() {
        return this.count;
    }

    public PagingRequest<C, E> setCount(Boolean count) {
        this.count = count;
        return this;
    }

    public PagingResult<E> getResult() {
        return this.result;
    }

    public PagingRequest<C, E> setResult(PagingResult result) {
        this.result = result;
        return this;
    }

    public String getDialect() {
        return dialect;
    }

    public PagingRequest<C, E> setDialect(String dialect) {
        this.dialect = dialect;
        return this;
    }

    public void clear() {
        clear(true);
    }

    public void clear(boolean clearResult) {
        count = null;
        useLastPageIfPageNoOut = null;
        ctx = null;
        if (clearResult) {
            if (result != null) {
                result.setItems(Collects.<E>emptyArrayList());
            }
        }
        setCondition(null);
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public String getCountColumn() {
        return countColumn;
    }

    public void setCountColumn(String countColumn) {
        this.countColumn = countColumn;
    }

    public PagingRequestContext getContext() {
        return ctx;
    }

    public PagingRequest setCtx(PagingRequestContext<C, E> ctx) {
        this.ctx = ctx;
        return this;
    }

    public Boolean isUseLastPageIfPageNoOut() {
        return useLastPageIfPageNoOut;
    }

    public void setUseLastPageIfPageNoOut(Boolean useLastPageIfPageNoOut) {
        this.useLastPageIfPageNoOut = useLastPageIfPageNoOut;
    }

    public String getSubqueryPagingStartFlag() {
        return subqueryPagingStartFlag;
    }

    public PagingRequest<C, E> setSubqueryPagingStartFlag(String subqueryPagingStartFlag) {
        this.subqueryPagingStartFlag = subqueryPagingStartFlag;
        return this;
    }

    public String getSubqueryPagingEndFlag() {
        return subqueryPagingEndFlag;
    }

    public PagingRequest<C, E> setSubqueryPagingEndFlag(String subqueryPagingEndFlag) {
        this.subqueryPagingEndFlag = subqueryPagingEndFlag;
        return this;
    }

    public boolean isSubQueryPaging() {
        return isSubQueryPaging;
    }

    public PagingRequest<C, E> subQueryPaging(boolean subQueryPaging) {
        isSubQueryPaging = subQueryPaging;
        return this;
    }


    public PagingRequest<C, E> subQueryPaging(String subQueryPagingStartFlag, String subQueryPagingEndFlag) {
        return subQueryPaging(true).setSubqueryPagingStartFlag(subQueryPagingStartFlag).setSubqueryPagingEndFlag(subQueryPagingEndFlag);
    }

    @Override
    public String toString() {
        return JSONBuilderProvider.create().serializeNulls(true).prettyFormat(true).addSerializationExclusion(new IgnoreAnnotationExclusion()).build().toJson(this);
    }
}
