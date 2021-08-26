package com.jn.sqlhelper.hibernate.dialect;

import com.jn.sqlhelper.dialect.internal.AbstractDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.pagination.LimitHandler;
/**
 * @since 3.6.1
 */
class HibernateDialectAdapter extends Dialect {
    private AbstractDialect delegate;

    public HibernateDialectAdapter(AbstractDialect delegate) {
        setDelegate(delegate);
    }

    public AbstractDialect getDelegate() {
        return delegate;
    }

    public void setDelegate(AbstractDialect delegate) {
        this.delegate = delegate;
    }

    @Override
    public LimitHandler getLimitHandler() {
        return new HibernateLimitHandlerAdapter(delegate);
    }

    @Override
    public boolean supportsLimit() {
        return delegate.isSupportsLimit();
    }

    @Override
    public boolean supportsLimitOffset() {
        return delegate.isSupportsLimitOffset();
    }

    @Override
    public boolean supportsVariableLimit() {
        return delegate.isSupportsVariableLimit();
    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return delegate.isBindLimitParametersInReverseOrder();
    }

    @Override
    public boolean bindLimitParametersFirst() {
        return delegate.isBindLimitParametersFirst();
    }

    @Override
    public boolean useMaxForLimit() {
        return delegate.isUseMaxForLimit();
    }

    @Override
    public boolean forceLimitUsage() {
        return delegate.isForceLimitUsage();
    }
}
