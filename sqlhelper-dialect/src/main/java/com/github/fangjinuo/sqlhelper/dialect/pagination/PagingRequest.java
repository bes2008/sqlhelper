
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

package com.github.fangjinuo.sqlhelper.dialect.pagination;


import java.util.Collections;

public class PagingRequest<E, R> {
    private Boolean count = null;
    private String countSqlId;
    private String dialect;
    private int pageNo;
    private int pageSize;
    private int fetchSize;
    private int timeout;
    private String orderBy;
    private E condition;
    private PagingResult<R> result;

    public String getCountSqlId() {
        return this.countSqlId;
    }

    public PagingRequest<E,R> setCountSqlId(String countSqlId) {
        this.countSqlId = countSqlId;
        return this;
    }

    public boolean isValidRequest() {
        if ((this.pageNo < 0) || (this.pageSize < 0)) {
            return false;
        }
        return true;
    }

    public PagingRequest<E,R> limit(int pageNo, int pageSize){
        return this.setPageNo(pageNo).setPageSize(pageSize);
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public PagingRequest<E, R> setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public PagingRequest<E, R> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public PagingRequest setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public PagingRequest<E,R> setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public PagingRequest<E,R> setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public boolean needOrderBy() {
        if ((this.orderBy == null) || (this.orderBy.trim().length() == 0)) {
            return false;
        }
        return true;
    }

    public E getCondition() {
        return (E) this.condition;
    }

    public PagingRequest<E,R> setCondition(E condition) {
        this.condition = condition;
        return this;
    }

    public Boolean getCount() {
        return this.count;
    }

    public PagingRequest<E,R> setCount(Boolean count) {
        this.count = count;
        return this;
    }

    public PagingResult<R> getResult() {
        return this.result;
    }

    public PagingRequest<E,R> setResult(PagingResult result) {
        this.result = result;
        return this;
    }

    public String getDialect() {
        return dialect;
    }

    public PagingRequest<E,R> setDialect(String dialect) {
        this.dialect = dialect;
        return this;
    }

    public void clear(){
        clear(true);
    }

    public void clear(boolean clearResult){
        if(clearResult) {
            if(result!=null) {
                result.setItems(Collections.EMPTY_LIST);
            }
        }
        setCondition(null);
    }
}
