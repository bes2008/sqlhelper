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


public class PaginationProperties {
    /**
     * 是否执行 select count()
     */
    private boolean count = true;
    /**
     * page size 默认值
     */
    private int defaultPageSize = 10;

    /**
     * 当查询页码超出最后一页时，是否采用最后一页
     */
    private boolean useLastPageIfPageOut = false;

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @Deprecated
    public void setUseLastPageIfPageNoOut(boolean useLastPageIfPageNoOut) {
        this.useLastPageIfPageOut = useLastPageIfPageNoOut;
    }

    public boolean isUseLastPageIfPageOut() {
        return useLastPageIfPageOut;
    }

    public void setUseLastPageIfPageOut(boolean useLastPageIfPageNoOut) {
        this.useLastPageIfPageOut = useLastPageIfPageNoOut;
    }


    @Override
    public String toString() {
        return "PaginationProperties{" +
                "count=" + count +
                ", defaultPageSize=" + defaultPageSize +
                ", useLastPageIfPageOut=" + useLastPageIfPageOut +
                '}';
    }
}
