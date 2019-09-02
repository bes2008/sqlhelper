package com.jn.sqlhelper.springjdbc;

import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaginationPreparedStatementSetter implements PreparedStatementSetter {
    private PreparedStatementSetter originalParameterSetter;
    public PaginationPreparedStatementSetter(PreparedStatementSetter delegate){
        originalParameterSetter = delegate;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        originalParameterSetter.setValues(ps);
    }
}
