package com.jn.sqlhelper.mango;

import com.jn.sqlhelper.dialect.PrepareParameterSetter;
import org.jfaster.mango.binding.BoundSql;
import org.jfaster.mango.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MangoPrepareStatementSetter implements PrepareParameterSetter<MangoQueryParameters> {
    @Override
    public int setParameters(PreparedStatement statement, MangoQueryParameters queryParameters, int startIndex) throws SQLException {
        BoundSql boundSql = queryParameters.getParameterValues();
        List<Object> args = boundSql.getArgs();
        List<TypeHandler<?>> typeHandlers = boundSql.getTypeHandlers();
        for (int i = 0; i < args.size(); i++) {
            TypeHandler typeHandler;
            typeHandler = typeHandlers.get(i);
            Object value = args.get(i);
            typeHandler.setParameter(statement, startIndex +i + 1, value);
        }
        return boundSql.getArgs().size();
    }
}
