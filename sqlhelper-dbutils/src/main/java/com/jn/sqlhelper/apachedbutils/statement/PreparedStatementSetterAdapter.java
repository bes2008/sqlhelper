package com.jn.sqlhelper.apachedbutils.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementSetterAdapter<P> implements PreparedStatementSetter {
    private com.jn.sqlhelper.common.statement.PreparedStatementSetter<P> delegate;
    private P parameters;

    public PreparedStatementSetterAdapter(com.jn.sqlhelper.common.statement.PreparedStatementSetter<P> delegate, P parameters) {
        this.delegate = delegate;
        this.parameters = parameters;
    }

    @Override
    public void setValues(PreparedStatement statement) throws SQLException {
        delegate.setParameters(statement, 1, parameters);
    }
}
