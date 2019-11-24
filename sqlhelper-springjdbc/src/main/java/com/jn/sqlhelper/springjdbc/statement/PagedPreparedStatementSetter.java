package com.jn.sqlhelper.springjdbc.statement;

import com.jn.sqlhelper.dialect.PagedPreparedParameterSetter;
import com.jn.sqlhelper.dialect.QueryParameters;
import com.jn.sqlhelper.dialect.pagination.PaginationPreparedStatement;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PagedPreparedStatementSetter implements PagedPreparedParameterSetter {
    private PreparedStatementSetter delegate;

    public PagedPreparedStatementSetter(PreparedStatementSetter setter) {
        delegate = setter;
    }

    @Override
    public int setOriginalParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
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

    @Override
    public int setBeforeSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        if (delegate != null) {

        }
        return queryParameters.getBeforeSubqueryParameterCount();
    }

    @Override
    public int setSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        return 0;
    }

    @Override
    public int setAfterSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        return queryParameters.getAfterSubqueryParameterCount();
    }
}