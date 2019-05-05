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

import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.RowSelectionBuilder;

public class PagingRequestBasedRowSelectionBuilder implements RowSelectionBuilder<PagingRequest> {
    @Override
    public RowSelection build(PagingRequest request)
            throws IllegalArgumentException {
        if (request.isValidRequest()) {
            RowSelection rowSelection = new RowSelection();
            rowSelection.setFetchSize(request.getFetchSize());
            rowSelection.setTimeout(request.getTimeout());
            rowSelection.setLimit(request.getPageSize());
            int pageNo = request.getPageNo();
            int offset = pageNo > 0 ? (pageNo - 1) * request.getPageSize() : 0;
            rowSelection.setOffset(offset);
            return rowSelection;
        }
        throw new IllegalArgumentException("PagingRequest is illegal");
    }
}

