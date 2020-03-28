/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.mybatis;

import com.jn.sqlhelper.dialect.instrument.SQLInstrumentConfig;
import com.jn.sqlhelper.mybatis.plugins.pagination.PaginationConfig;

public class SqlHelperMybatisProperties {

    private SQLInstrumentConfig instrumentor = new SQLInstrumentConfig();
    private PaginationConfig pagination = new PaginationConfig();

    public SQLInstrumentConfig getInstrumentor() {
        return instrumentor;
    }

    public void setInstrumentor(SQLInstrumentConfig instrumentor) {
        this.instrumentor = instrumentor;
    }

    public PaginationConfig getPagination() {
        return pagination;
    }

    public void setPagination(PaginationConfig pagination) {
        this.pagination = pagination;
    }

    @Override
    public String toString() {
        return "SqlHelperMybatisProperties{" +
                "instrumentor=" + instrumentor +
                ", pagination=" + pagination +
                '}';
    }
}
