
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

import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Functions;

public class PagingResult<E> extends com.jn.langx.util.pagination.PagingResult<E> {

    public static final <I, O> PagingResult<O> fromCommonPagingResult(com.jn.langx.util.pagination.PagingResult<I> from, Function<I, O> mapper) {
        PagingResult<O> to = new PagingResult<O>();
        to.setPageNo(from.getPageNo());
        to.setPageSize(from.getPageSize());
        to.setTotal(from.getTotal());
        Function fun = mapper;
        if (fun == null) {
            fun = Functions.noopFunction();
        }
        to.setItems(Pipeline.of(from.getItems()).map(fun).asList());
        return to;
    }

    public static final <I, O> com.jn.langx.util.pagination.PagingResult<O> fromDatabasePagingResult(PagingResult<I> from, Function<I, O> mapper) {
        com.jn.langx.util.pagination.PagingResult<O> to = new com.jn.langx.util.pagination.PagingResult<O>();
        to.setPageNo(from.getPageNo());
        to.setPageSize(from.getPageSize());
        to.setTotal(from.getTotal());
        Function fun = mapper;
        if (fun == null) {
            fun = Functions.noopFunction();
        }
        to.setItems(Pipeline.of(from.getItems()).map(fun).asList());
        return to;
    }
}
