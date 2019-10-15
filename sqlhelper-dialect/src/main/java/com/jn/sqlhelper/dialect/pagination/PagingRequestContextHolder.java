
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

import com.jn.sqlhelper.dialect.RowSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagingRequestContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(PagingRequestContextHolder.class);

    private final ThreadLocal<PagingRequestContext> variables = new ThreadLocal<PagingRequestContext>();

    private static final PagingRequestContextHolder INSTANCE = new PagingRequestContextHolder();

    public static PagingRequestContextHolder getContext() {
        return INSTANCE;
    }

    private PagingRequestContext newOne() {
        try {
            return PagingRequestContext.class.newInstance();
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public void setPagingRequest(PagingRequest request) {
        PagingRequestContext context = get();
        if (context == null) {
            context = newOne();
            if (context == null) {
                this.variables.remove();
            }
        }

        if (context != null) {
            this.variables.set(context);
            context.setRequest(request);
        }
    }

    public void setRowSelection(RowSelection rowSelection) {
        PagingRequestContext context = get();
        if (context == null) {
            context = newOne();
            if (context == null) {
                this.variables.remove();
            }
        }
        if (context != null) {
            this.variables.set(context);
            context.setRowSelection(rowSelection);
        }
    }

    public PagingRequestContext get() {
        return this.variables.get();
    }

    public void set(PagingRequestContext ctx) {
        this.variables.set(ctx);
    }

    public PagingRequest getPagingRequest() {
        PagingRequestContext context = get();
        if (context != null) {
            return context.getRequest();
        }
        return null;
    }

    public RowSelection getRowSelection() {
        PagingRequestContext context = get();
        if (context != null) {
            return context.getRowSelection();
        }
        return null;
    }

    public void remove() {
        this.variables.remove();
    }

    public boolean isPagingRequest() {
        return getPagingRequest() != null;
    }

    public boolean isOrderByRequest() {
        if (!isPagingRequest()) {
            return false;
        }
        PagingRequest request = getPagingRequest();
        if (!request.needOrderBy()) {
            return false;
        }
        if (request.getOrderByAsString().contains("?")) {
            return false;
        }
        return true;
    }
}
