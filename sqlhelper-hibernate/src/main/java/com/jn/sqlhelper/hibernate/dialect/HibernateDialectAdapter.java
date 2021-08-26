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
}
