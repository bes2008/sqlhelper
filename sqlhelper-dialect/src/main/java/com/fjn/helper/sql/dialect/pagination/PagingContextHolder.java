package com.fjn.helper.sql.dialect.pagination;

import com.fjn.helper.sql.dialect.RowSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PagingContextHolder<E extends PagingRequestContext> {
    private static final Logger logger = LoggerFactory.getLogger(PagingContextHolder.class);

    private final ThreadLocal<E> variables = new ThreadLocal();

    private static final PagingContextHolder INSTANCE = new PagingContextHolder();
    private Class<E> clazz;

    public static PagingContextHolder getContext() {
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
