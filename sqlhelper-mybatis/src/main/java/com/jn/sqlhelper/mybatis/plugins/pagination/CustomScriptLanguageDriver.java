package com.jn.sqlhelper.mybatis.plugins.pagination;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

public class CustomScriptLanguageDriver extends XMLLanguageDriver {
    @Override
    public ParameterHandler createParameterHandler(final MappedStatement mappedStatement, final Object parameterObject, final BoundSql boundSql) {
        return new CustomMybatisParameterHandler(mappedStatement, parameterObject, boundSql);
    }
}
