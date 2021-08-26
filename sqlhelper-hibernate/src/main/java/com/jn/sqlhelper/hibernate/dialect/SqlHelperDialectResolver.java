package com.jn.sqlhelper.hibernate.dialect;

import com.jn.sqlhelper.dialect.internal.AbstractDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import com.jn.sqlhelper.dialect.DialectRegistry;

public class SqlHelperDialectResolver implements DialectResolver {
    @Override
    public Dialect resolveDialect(DialectResolutionInfo info) {
        AbstractDialect sqlHelperDialect = (AbstractDialect) DialectRegistry.getInstance().getDialectByResolutionInfo(new HibernateDialectResolutionInfoAdapter(info));
        return new HibernateDialectAdapter(sqlHelperDialect);
    }
}
