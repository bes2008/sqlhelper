/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 2.1 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fjn.helper.sql.dialect.pagination;

import java.util.List;


public class PagingResult<E> {
    private int pageNo;
    private int pageSize;
    private int total;
    private List<E> items;

    public int getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<E> getItems() {
        return this.items;
    }

    public void setItems(List<E> items) {
        this.items = items;
    }

    public int getPageCount() {
        if ((this.total <= 0) || (this.pageSize <= 0)) {
            return 0;
        }
        return this.total / this.pageSize + (this.total % this.pageSize == 0 ? 0 : 1);
    }
}
