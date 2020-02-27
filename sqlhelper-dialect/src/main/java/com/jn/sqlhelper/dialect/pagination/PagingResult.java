
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

import java.util.List;

public class PagingResult<E> {
    private int pageNo;
    private int pageSize;
    private long total;
    private List<E> items;

    public int getPageNo() {
        return this.pageNo;
    }

    public PagingResult<E> setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public PagingResult<E> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public long getTotal() {
        return this.total;
    }

    public PagingResult<E> setTotal(long total) {
        this.total = total;
        return this;
    }

    public List<E> getItems() {
        return this.items;
    }

    public PagingResult<E> setItems(List<E> items) {
        this.items = items;
        return this;
    }

    public int getMaxPage() {
        return Long.valueOf(getMaxPageCount(pageSize)).intValue();
    }

    @Deprecated
    public long getMaxPageCount() {
        return getMaxPageCount(pageSize);
    }

    public long getMaxPageCount(int pageSize) {
        // unknown
        if (this.total < 0) {
            return -1;
        }
        if ((this.total == 0) || (this.pageSize == 0)) {
            return 0;
        }
        if (pageSize < 0) {
            return -1;
        }
        return this.total / this.pageSize + (this.total % this.pageSize == 0 ? 0 : 1);
    }
}
