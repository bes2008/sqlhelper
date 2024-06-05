package com.jn.sqlhelper.springjdbc.statement;

import com.jn.langx.util.Emptys;
import com.jn.sqlhelper.dialect.pagination.PagedPreparedParameterSetter;
import com.jn.sqlhelper.dialect.pagination.QueryParameters;
import com.jn.sqlhelper.dialect.pagination.PagedPreparedStatement;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@SuppressWarnings("unchecked")
public class PagedPreparedStatementSetter implements PagedPreparedParameterSetter {
    private PreparedStatementSetter delegate;

    public PagedPreparedStatementSetter(PreparedStatementSetter setter) {
        delegate = setter;
    }

    @Override
    public int setOriginalParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        if (delegate != null) {
            if ((statement instanceof PagedPreparedStatement)) {
                PagedPreparedStatement pps = (PagedPreparedStatement) statement;
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
        PagedPreparedParameterSetter setter = getPaginationSetter(statement);
        if (setter!=null) {
            return setter.setBeforeSubqueryParameters(statement, queryParameters, startIndex);
        }
        return 0;
    }

    @Override
    public int setSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        PagedPreparedParameterSetter setter = getPaginationSetter(statement);
        if (setter!=null) {
            return setter.setSubqueryParameters(statement, queryParameters, startIndex);
        }
        return 0;
    }

    @Override
    public int setAfterSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        PagedPreparedParameterSetter setter = getPaginationSetter(statement);
        if (setter!=null) {
            return setter.setAfterSubqueryParameters(statement, queryParameters, startIndex);
        }
        return 0;
    }


    private PagedPreparedParameterSetter getPaginationSetter(PreparedStatement statement) {
        if (delegate != null && delegate instanceof PagedPreparedParameterSetter && statement instanceof PagedPreparedStatement) {
            return (PagedPreparedParameterSetter) delegate;
        }
        return null;
    }

}