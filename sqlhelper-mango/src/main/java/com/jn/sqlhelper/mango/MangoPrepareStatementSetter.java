package com.jn.sqlhelper.mango;

import com.jn.sqlhelper.dialect.PagedPreparedParameterSetter;
import org.jfaster.mango.binding.BoundSql;
import org.jfaster.mango.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MangoPrepareStatementSetter implements PagedPreparedParameterSetter<MangoQueryParameters> {
    @Override
    public int setOriginalParameters(PreparedStatement statement, MangoQueryParameters queryParameters, int startIndex) throws SQLException {
        BoundSql boundSql = queryParameters.getParameterValues();
        List<Object> args = boundSql.getArgs();
        List<TypeHandler<?>> typeHandlers = boundSql.getTypeHandlers();
        for (int i = 0; i < args.size(); i++) {
            TypeHandler typeHandler;
            typeHandler = typeHandlers.get(i);
            Object value = args.get(i);
            typeHandler.setParameter(statement, startIndex + i + 1, value);
        }
        return boundSql.getArgs().size();
    }

    @Override
    public int setBeforeSubqueryParameters(PreparedStatement statement, MangoQueryParameters queryParameters, int startIndex) throws SQLException {
        return queryParameters.getBeforeSubqueryParameterCount();
    }


    @Override
    public int setSubqueryParameters(PreparedStatement statement, MangoQueryParameters queryParameters, int startIndex) throws SQLException {
        return 0;
    }

    @Override
    public int setAfterSubqueryParameters(PreparedStatement statement, MangoQueryParameters queryParameters, int startIndex) throws SQLException {
        return queryParameters.getAfterSubqueryParameterCount();
    }

}
