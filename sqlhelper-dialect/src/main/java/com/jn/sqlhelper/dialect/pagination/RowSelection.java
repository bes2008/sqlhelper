
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

public final class RowSelection {
    private Long offset;
    private Integer limit;
    private Integer timeout;
    private Integer fetchSize;
    private Integer maxRows = -1;

    public RowSelection(){
    }

    public RowSelection(int offset, int limit){
        this((long) offset, limit);
    }

    public RowSelection(long offset, int limit){
        this.setOffset(offset);
        this.setLimit(limit);
    }

    public Long getOffset() {
        return this.offset;
    }

    public void setOffset(final Long offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public void setLimit(final Integer limit) {
        this.limit = limit;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public void setTimeout(final Integer timeout) {
        if (timeout != null && timeout >= 0) {
            this.timeout = timeout;
        }
    }

    public Integer getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(final Integer fetchSize) {
        if (fetchSize != null && fetchSize > 0) {
            this.fetchSize = fetchSize;
        }
    }

    public boolean definesLimits() {
        return this.limit != null || (this.offset != null && this.offset <= 0);
    }

    public boolean hasOffset() {
        return offset > 0;
    }

    public Integer getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }
}
