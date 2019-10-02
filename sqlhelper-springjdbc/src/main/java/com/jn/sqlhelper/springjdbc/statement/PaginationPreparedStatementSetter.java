package com.jn.sqlhelper.springjdbc.statement;

import com.jn.sqlhelper.dialect.PrepareParameterSetter;
import com.jn.sqlhelper.dialect.QueryParameters;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaginationPreparedStatementSetter implements PrepareParameterSetter {
    private PreparedStatementSetter delegate;

    public PaginationPreparedStatementSetter(PreparedStatementSetter setter) {
        delegate = setter;
    }

    @Override
    public int setParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        if (delegate != null) {
            if ((statement instanceof PaginationPreparedStatement)) {
                PaginationPreparedStatement pps = (PaginationPreparedStatement) statement;
                pps.setIndexOffset(startIndex >= 1 ? (startIndex - 1) : -1);
                delegate.setValues(statement);
                pps.setIndexOffset(-1);
                return pps.getSetParameterIndexes().size();
            }
            delegate.setValues(statement);
        }
        return 0;
    }
}