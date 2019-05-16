package com.fjn.helper.sql.mybatis.plugins.pagination;

import com.fjn.helper.sql.dialect.PrepareParameterSetter;
import com.fjn.helper.sql.dialect.QueryParameters;
import com.fjn.helper.sql.dialect.pagination.PagingRequestContextHolder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CustomMybatisParameterHandler implements ParameterHandler, PrepareParameterSetter {
    private static final Logger logger = LoggerFactory.getLogger(CustomMybatisParameterHandler.class);
    private static final PagingRequestContextHolder<MybatisPaginationRequestContext> PAGING_CONTEXT = (PagingRequestContextHolder<MybatisPaginationRequestContext>) PagingRequestContextHolder.getContext();

    private final TypeHandlerRegistry typeHandlerRegistry;
    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private final BoundSql boundSql;
    private final Configuration configuration;

    public CustomMybatisParameterHandler(final MappedStatement mappedStatement, final Object parameterObject, final BoundSql boundSql) {
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    @Override
    public Object getParameterObject() {
        return this.parameterObject;
    }

    private boolean isPagingCountStatement() {
        final MybatisPaginationRequestContext requestContext = PAGING_CONTEXT.get();
        return requestContext.getCountSql() == this.boundSql;
    }

    @Override
    public void setParameters(final PreparedStatement ps) {
        if (PAGING_CONTEXT.getPagingRequest() == null || this.isPagingCountStatement()) {
            this.setOriginalParameters(ps, 1);
            return;
        }
        try {
            final MybatisQueryParameters queryParameters = new MybatisQueryParameters();
            queryParameters.setRowSelection(PAGING_CONTEXT.getRowSelection());
            queryParameters.setCallable(this.mappedStatement.getStatementType() == StatementType.CALLABLE);
            queryParameters.setParameters(this.getParameterObject());
            MybatisPaginationPlugin.getInstrumentor().bindParameters(ps, this, queryParameters, true);
        } catch (SQLException ex) {
            logger.error("errorCode:{},message:{}", ex.getErrorCode(), ex.getMessage(), ex);
        }
    }

    @Override
    public int setParameters(final PreparedStatement ps, final QueryParameters parameters, final int startIndex) {
        this.setOriginalParameters(ps, startIndex);
        return this.boundSql.getParameterMappings().size();
    }

    private void setOriginalParameters(final PreparedStatement ps, final int startIndex) {
        ErrorContext.instance().activity("setting parameters").object(this.mappedStatement.getParameterMap().getId());
        final List<ParameterMapping> parameterMappings = this.boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); ++i) {
                final ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    final String propertyName = parameterMapping.getProperty();
                    Object value;
                    if (this.boundSql.hasAdditionalParameter(propertyName)) {
                        value = this.boundSql.getAdditionalParameter(propertyName);
                    } else if (this.parameterObject == null) {
                        value = null;
                    } else if (this.typeHandlerRegistry.hasTypeHandler(this.parameterObject.getClass())) {
                        value = this.parameterObject;
                    } else {
                        final MetaObject metaObject = this.configuration.newMetaObject(this.parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    final TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) {
                        jdbcType = this.configuration.getJdbcTypeForNull();
                    }
                    try {
                        typeHandler.setParameter(ps, i + startIndex, value, jdbcType);
                    } catch (TypeException e) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    }catch (SQLException e){
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    }
                }
            }
        }
    }
}
