
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

import com.jn.sqlhelper.dialect.orderby.OrderBy;

import java.util.ArrayList;

public class PagingRequest<E, R> {
    private Boolean count = null;
    private String countSqlId;
    private String dialect;
    // begin 1
    private int pageNo = 1;
    // pageSize < 0, the limit is Integer.MAX
    // pageSize =0, is Empty paging request, the limit is 0
    // pageSize > 0, the limit is pageSize
    private int pageSize;
    private Integer fetchSize;
    private int timeout;
    private OrderBy orderBy;
    private E condition;
    private PagingResult<R> result;

    public String getCountSqlId() {
        return this.countSqlId;
    }

    public PagingRequest<E, R> setCountSqlId(String countSqlId) {
        this.countSqlId = countSqlId;
        return this;
    }

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

    public PagingRequest<E, R> limit(int pageNo, int pageSize) {
        return this.setPageNo(pageNo).setPageSize(pageSize);
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public PagingRequest<E, R> setPageNo(int pageNo) {
        if (pageNo <= 0) {
            return this;
        }
        this.pageNo = pageNo;
        return this;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public PagingRequest<E, R> setPageSize(int pageSize) {
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

    public PagingRequest<E, R> setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getOrderByAsString() {
        return this.orderBy.toString();
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public PagingRequest<E, R> setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public boolean needOrderBy() {
        if (this.orderBy == null || !this.orderBy.isValid()) {
            return false;
        }
        return true;
    }

    public E getCondition() {
        return (E) this.condition;
    }

    public PagingRequest<E, R> setCondition(E condition) {
        this.condition = condition;
        return this;
    }

    public Boolean needCount() {
        return this.count;
    }

    public PagingRequest<E, R> setCount(Boolean count) {
        this.count = count;
        return this;
    }

    public PagingResult<R> getResult() {
        return this.result;
    }

    public PagingRequest<E, R> setResult(PagingResult result) {
        this.result = result;
        return this;
    }

    public String getDialect() {
        return dialect;
    }

    public PagingRequest<E, R> setDialect(String dialect) {
        this.dialect = dialect;
        return this;
    }

    public void clear() {
        clear(true);
    }

    public void clear(boolean clearResult) {
        if (clearResult) {
            if (result != null) {
                result.setItems(new ArrayList());
            }
        }
        setCondition(null);
    }
}
