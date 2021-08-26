package com.jn.sqlhelper.hibernate.dialect;

import com.jn.sqlhelper.dialect.internal.AbstractDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import com.jn.sqlhelper.dialect.DialectRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 3.6.1
 */
public class SqlHelperDialectResolver implements DialectResolver {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelperDialectResolver.class);

    @Override
    public Dialect resolveDialect(DialectResolutionInfo info) {
        AbstractDialect sqlHelperDialect = (AbstractDialect) DialectRegistry.getInstance().getDialectByResolutionInfo(new HibernateDialectResolutionInfoAdapter(info));
        if (sqlHelperDialect != null) {
            Dialect dialect = new HibernateDialectAdapter(sqlHelperDialect);
            logger.info("Using SQLHelper dialect {} for hibernate", sqlHelperDialect);
            return dialect;
        }
        return null;
    }
}
