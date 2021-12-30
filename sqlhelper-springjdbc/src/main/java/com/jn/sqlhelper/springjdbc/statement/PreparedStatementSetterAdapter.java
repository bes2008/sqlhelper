package com.jn.sqlhelper.springjdbc.statement;

import org.springframework.jdbc.core.PreparedStatementSetter;

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
    public void setValues(PreparedStatement ps) throws SQLException {
        delegate.setParameters(ps, 1, parameters);
    }
}
