
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

package com.fjn.helper.sql.dialect.pagination;

import com.fjn.helper.sql.dialect.RowSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagingRequestContextHolder<E extends PagingRequestContext> {
    private static final Logger logger = LoggerFactory.getLogger(PagingRequestContextHolder.class);

    private final ThreadLocal<E> variables = new ThreadLocal<E>();

    private static final PagingRequestContextHolder INSTANCE = new PagingRequestContextHolder();
    private Class<E> clazz;

    public static PagingRequestContextHolder getContext() {
        return INSTANCE;
    }


    public void setContextClass(Class<E> clazz) {
        this.clazz = clazz;
    }

    private E newOne() {
        try {
            return (E) this.clazz.newInstance();
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public void setPagingRequest(PagingRequest request) {
        E context = get();
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
        E context = get();
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

    public E get() {
        return (E) this.variables.get();
    }

    public void set(E ctx) {
        this.variables.set(ctx);
    }

    public PagingRequest getPagingRequest() {
        E context = get();
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
}
