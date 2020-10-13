
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

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.dialect.SelectRequest;

public class PagingRequest<C, E> extends SelectRequest<PagingRequest<C, E>, PagingRequestContext<C, E>> {
    private static final long serialVersionUID = 1L;
    /**
     * 是否先执行 select count()
     */
    private Boolean count = null;

    /**
     * select count(countColumn)
     */
    private String countColumn;

    /**
     * count sql 要不要缓存
     */
    private Boolean cacheCount = null;

    // begin 1
    private int pageNo = 1;
    // pageSize < 0, the limit is Integer.MAX
    // pageSize =0, is Empty paging request, the limit is 0
    // pageSize > 0, the limit is pageSize
    private int pageSize;

    /**
     * 当查询的页码超出实际的页码时，是否采用最后一页数据
     */
    private Boolean useLastPageIfPageOut;
    /**
     * 查询条件
     */
    private C condition;
    /**
     * 分页结果
     */
    private transient PagingResult<E> result;
    /**
     * 是否为子查询分页
     */
    private boolean isSubQueryPaging = false;
    /**
     * 要分页的子查询语句的起始标记
     */
    private String subqueryPagingStartFlag;

    /**
     * 要分页的子查询语句的结束标记
     */
    private String subqueryPagingEndFlag;


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

    public PagingRequest<C, E> setResult(PagingResult<E> result) {
        this.result = result;
        return this;
    }

    public void clear() {
        clear(true);
    }

    public void clear(boolean clearResult) {
        super.clear();
        count = null;
        useLastPageIfPageOut = null;
        setCtx(null);
        if (clearResult) {
            if (result != null) {
                result.setItems(Collects.<E>emptyArrayList());
            }
        }
        setCondition(null);
    }


    public String getCountColumn() {
        return countColumn;
    }

    public void setCountColumn(String countColumn) {
        this.countColumn = countColumn;
    }

    public Boolean getCacheCount() {
        return cacheCount;
    }

    public PagingRequest<C, E> setCacheCount(Boolean cacheCount) {
        this.cacheCount = cacheCount;
        return this;
    }

    public PagingRequest<C, E> setCtx(PagingRequestContext ctx) {
        return (PagingRequest) setContext(ctx);
    }

    public Boolean isUseLastPageIfPageOut() {
        return useLastPageIfPageOut;
    }

    public PagingRequest<C, E> setUseLastPageIfPageOut(Boolean useLastPageIfPageNoOut) {
        this.useLastPageIfPageOut = useLastPageIfPageNoOut;
        return this;
    }

    /**
     * @param useLastPageIfPageNoOut
     * @return
     * @see #setUseLastPageIfPageOut(Boolean)
     */
    @Deprecated
    public PagingRequest<C, E> setUseLastPageIfPageNoOut(Boolean useLastPageIfPageNoOut) {
        return setUseLastPageIfPageOut(useLastPageIfPageNoOut);
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

    public boolean isSubqueryPaging() {
        return isSubQueryPaging;
    }

    public PagingRequest<C, E> subqueryPaging(boolean subQueryPaging) {
        isSubQueryPaging = subQueryPaging;
        return this;
    }


    public PagingRequest<C, E> subqueryPaging(String subQueryPagingStartFlag, String subQueryPagingEndFlag) {
        return subqueryPaging(true).setSubqueryPagingStartFlag(subQueryPagingStartFlag).setSubqueryPagingEndFlag(subQueryPagingEndFlag);
    }

    @Override
    public String toString() {
        return "PagingRequest{" +
                "count=" + count +
                ", countColumn='" + countColumn + '\'' +
                ", cacheCount=" + cacheCount +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", useLastPageIfPageOut=" + useLastPageIfPageOut +
                ", condition=" + condition +
                ", result=" + result +
                ", isSubQueryPaging=" + isSubQueryPaging +
                ", subqueryPagingStartFlag='" + subqueryPagingStartFlag + '\'' +
                ", subqueryPagingEndFlag='" + subqueryPagingEndFlag + '\'' +
                ", dialect='" + getDialect() + '\'' +
                '}';
    }

    @Override
    public boolean isPagingRequest() {
        return true;
    }
}
