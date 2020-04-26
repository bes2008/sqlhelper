package com.jn.sqlhelper.dialect.instrument.tenant;

import com.jn.langx.lifecycle.InitializationException;
import com.jn.sqlhelper.dialect.instrument.AbstractClauseTransformer;
import com.jn.sqlhelper.dialect.instrument.TransformConfig;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

/**
 * @author huxiongming
 */
public class DefaultTenantTransformer extends AbstractClauseTransformer implements TenantTransformer{

    @Override
    public void init() throws InitializationException {
    }
    @Override
    public SqlStatementWrapper transform(SqlStatementWrapper statement, TransformConfig config) {
        TenantTransformer tenantTransformer=getInstrumentation().getTenantTransformer();
        return tenantTransformer.transform(statement, config);
    }

}
