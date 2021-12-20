package com.jn.sqlhelper.mybatis.plugins;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.sqlhelper.dialect.SqlRequestContext;
import com.jn.sqlhelper.dialect.SqlRequestContextHolder;
import com.jn.sqlhelper.dialect.likeescaper.LikeEscaper;
import com.jn.sqlhelper.dialect.pagination.PagedPreparedParameterSetter;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContext;
import com.jn.sqlhelper.dialect.pagination.PagingRequestContextHolder;
import com.jn.sqlhelper.dialect.pagination.QueryParameters;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
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

@SuppressWarnings("unchecked")
public class CustomMybatisParameterHandler implements ParameterHandler, PagedPreparedParameterSetter {
    private static final Logger logger = LoggerFactory.getLogger(CustomMybatisParameterHandler.class);
    private static final PagingRequestContextHolder PAGING_CONTEXT = PagingRequestContextHolder.getContext();

    protected final TypeHandlerRegistry typeHandlerRegistry;
    protected final MappedStatement mappedStatement;
    protected Object parameterObject;
    protected final BoundSql boundSql;
    protected final Configuration configuration;

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

    protected Object getOriginParameterObject() {
        return this.parameterObject;
    }

    private boolean isPagingCountStatement() {
        final PagingRequestContext requestContext = PAGING_CONTEXT.get();
        return requestContext.get(MybatisSqlRequestContextKeys.COUNT_SQL) == this.boundSql;
    }

    private boolean isInPagingRequestScope() {
        return PAGING_CONTEXT.getPagingRequest() != null;
    }

    private boolean isInvalidPagingRequest() {
        return !PAGING_CONTEXT.getPagingRequest().isValidRequest();
    }

    private List<Integer> getEscapeLikeParametersIndexes() {
        SqlRequestContext sqlRequestContext = SqlRequestContextHolder.getInstance().get();
        if (Objs.isNull(sqlRequestContext) || Objs.isNull(sqlRequestContext.getRequest())) {
            return null;
        }
        if (Objs.isNull(sqlRequestContext.get(MybatisSqlRequestContextKeys.LIKE_ESCAPER))) {
            return null;
        }
        return (List<Integer>) sqlRequestContext.get(MybatisSqlRequestContextKeys.LIKE_ESCAPE_PARAMETERS_INDEXES);
    }

    @Override
    public void setParameters(final PreparedStatement ps) {
        // not a pagination request
        if (!MybatisUtils.isQueryStatement(mappedStatement)
                || !isInPagingRequestScope()
                || isInvalidPagingRequest()
                || this.isPagingCountStatement()
                || NestedStatements.isNestedStatement(mappedStatement)) {
            this.setParameters(ps, this.boundSql.getParameterMappings(), 1, getEscapeLikeParametersIndexes());
            return;
        }
        // a pagination request
        try {
            final MybatisQueryParameters queryParameters = new MybatisQueryParameters();
            queryParameters.setRowSelection(PAGING_CONTEXT.getRowSelection());
            queryParameters.setCallable(MybatisUtils.isCallableStatement(this.mappedStatement));
            PagingRequestContext request = PAGING_CONTEXT.get();
            queryParameters.setParameters(this.getParameterObject(), request.getInteger(PagingRequestContext.BEFORE_SUBQUERY_PARAMETERS_COUNT, 0), request.getInteger(PagingRequestContext.AFTER_SUBQUERY_PARAMETERS_COUNT, 0));
            SqlHelperMybatisPlugin.getInstrumentor().bindParameters(ps, this, queryParameters, true);
        } catch (SQLException ex) {
            logger.error("errorCode:{},message:{}", ex.getErrorCode(), ex.getMessage(), ex);
        }
    }

    @Override
    public int setOriginalParameters(final PreparedStatement ps, final QueryParameters parameters, final int startIndex) {
        final List<ParameterMapping> parameterMappings = this.boundSql.getParameterMappings();
        setParameters(ps, parameterMappings, startIndex, getEscapeLikeParametersIndexes());
        return this.boundSql.getParameterMappings().size();
    }

    private void setParameters(final PreparedStatement ps, List<ParameterMapping> parameterMappings, final int startIndex, List<Integer> escapeLikeParametersIndexes) {
        boolean needEscapeLikeParameters = Emptys.isNotEmpty(escapeLikeParametersIndexes);
        LikeEscaper likeEscaper = null;
        if (needEscapeLikeParameters) {
            likeEscaper = (LikeEscaper) SqlRequestContextHolder.getInstance().get().get(MybatisSqlRequestContextKeys.LIKE_ESCAPER);
        }
        ErrorContext.instance().activity("setting parameters").object(this.mappedStatement.getParameterMap().getId());
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); ++i) {
                final ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    final String propertyName = parameterMapping.getProperty();
                    Object value;
                    Object parameterObject = getOriginParameterObject();
                    if (this.boundSql.hasAdditionalParameter(propertyName)) {
                        value = this.boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (this.typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        final MetaObject metaObject = this.configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    final TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    if (value == null && jdbcType == null) {
                        jdbcType = this.configuration.getJdbcTypeForNull();
                    }
                    try {
                        if (value instanceof String && needEscapeLikeParameters) {
                            if (escapeLikeParametersIndexes.contains(i + startIndex - 1)) {
                                value = likeEscaper.escape(value.toString());
                            }
                        }
                        typeHandler.setParameter(ps, i + startIndex, value, jdbcType);
                    } catch (TypeException e) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    } catch (SQLException e) {
                        throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    }
                }
            }
        }
    }


    @Override
    public int setBeforeSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        // find before parameters
        final List<ParameterMapping> parameterMappings = this.boundSql.getParameterMappings();
        List<ParameterMapping> before = Collects.limit(parameterMappings, queryParameters.getBeforeSubqueryParameterCount());
        setParameters(statement, before, startIndex, getEscapeLikeParametersIndexes());
        return queryParameters.getBeforeSubqueryParameterCount();
    }

    @Override
    public int setSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        final List<ParameterMapping> parameterMappings = this.boundSql.getParameterMappings();
        List<ParameterMapping> subquery = Pipeline.of(parameterMappings)
                .limit(parameterMappings.size() - queryParameters.getAfterSubqueryParameterCount())
                .skip(queryParameters.getBeforeSubqueryParameterCount())
                .asList();
        setParameters(statement, subquery, startIndex, getEscapeLikeParametersIndexes());
        return subquery.size();
    }

    @Override
    public int setAfterSubqueryParameters(PreparedStatement statement, QueryParameters queryParameters, int startIndex) throws SQLException {
        final List<ParameterMapping> parameterMappings = this.boundSql.getParameterMappings();
        List<ParameterMapping> after = Collects.skip(parameterMappings, parameterMappings.size() - queryParameters.getAfterSubqueryParameterCount());
        setParameters(statement, after, startIndex, getEscapeLikeParametersIndexes());
        return queryParameters.getBeforeSubqueryParameterCount();
    }
}
