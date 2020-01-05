
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

import com.jn.langx.util.Objects;
import com.jn.langx.util.function.Consumer;
import com.jn.sqlhelper.dialect.RowSelection;
import com.jn.sqlhelper.dialect.SqlRequestContext;
import com.jn.sqlhelper.dialect.SqlRequestContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagingRequestContextHolder extends SqlRequestContextHolder {
    private static final Logger logger = LoggerFactory.getLogger(PagingRequestContextHolder.class);

    private static final PagingRequestContextHolder INSTANCE = new PagingRequestContextHolder();

    private PagingRequestContextHolder() {
    }

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

    public void setPagingRequest(final PagingRequest request) {
        setContextContent(new Consumer<PagingRequestContext>() {
            @Override
            public void accept(PagingRequestContext context) {
                context.setRequest(request);
                request.setCtx(context);
            }
        });
    }

    public void setRowSelection(final RowSelection rowSelection) {
        setContextContent(new Consumer<PagingRequestContext>() {
            @Override
            public void accept(PagingRequestContext context) {
                context.setRowSelection(rowSelection);
            }
        });
    }

    private <X> void setContextContent(Consumer<PagingRequestContext> consumer) {
        PagingRequestContext context = get();
        if (Objects.isNull(context)) {
            context = newOne();
            if (Objects.isNull(context)) {
                this.variables.remove();
            }
        }
        if (Objects.isNotNull(context)) {
            this.variables.set(context);
            consumer.accept(context);
        }
    }

    public PagingRequestContext get() {
        SqlRequestContext context = this.variables.get();
        if (Objects.isNotNull(context) && context.isPagingRequest()) {
            return (PagingRequestContext) context;
        }
        return null;
    }

    public void set(PagingRequestContext ctx) {
        this.variables.set(ctx);
    }

    public PagingRequest getPagingRequest() {
        PagingRequestContext context = get();
        if (Objects.isNotNull(context)) {
            return context.getRequest();
        }
        return null;
    }

    public RowSelection getRowSelection() {
        PagingRequestContext context = get();
        if (Objects.isNotNull(context)) {
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

}
