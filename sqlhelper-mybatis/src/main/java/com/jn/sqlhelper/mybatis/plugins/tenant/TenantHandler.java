package com.jn.sqlhelper.mybatis.plugins.tenant;

import com.jn.langx.pipeline.AbstractHandler;
import com.jn.langx.pipeline.HandlerContext;
import com.jn.langx.pipeline.Pipelines;

import com.jn.langx.util.Chars;
import com.jn.langx.util.Emptys;
import com.jn.sqlhelper.dialect.SqlRequestContext;
import com.jn.sqlhelper.dialect.SqlRequestContextHolder;
import com.jn.sqlhelper.dialect.instrument.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.tenant.Tenant;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import com.jn.sqlhelper.mybatis.PluginUtils;
import com.jn.sqlhelper.mybatis.plugins.ExecutorInvocation;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author huxiongming
 */
public class TenantHandler extends AbstractHandler{
    private static Logger logger = LoggerFactory.getLogger(TenantHandler.class);
    private static final String TENANT_SUFFIX = "_tenant";

    @Override
    public void inbound(HandlerContext ctx) throws Throwable {
        SqlRequestContext sqlContext = SqlRequestContextHolder.getInstance().get();
        ExecutorInvocation executorInvocation = (ExecutorInvocation) ctx.getPipeline().getTarget();
        MappedStatement mappedStatement = executorInvocation.getMappedStatement();
        final Tenant tenant=sqlContext.getRequest().getTenant();
        if (MybatisUtils.isPreparedStatement(mappedStatement)&&Emptys.isNotEmpty(sqlContext.getRequest().getTenant())) {
            intercept(ctx);
        }else{
            Pipelines.skipHandler(ctx, true);
        }
    }


    private void intercept(HandlerContext ctx) {
        SqlRequestContext sqlContext = SqlRequestContextHolder.getInstance().get();
        ExecutorInvocation executorInvocation = (ExecutorInvocation) ctx.getPipeline().getTarget();
        MappedStatement ms = executorInvocation.getMappedStatement();
        BoundSql boundSql = executorInvocation.getBoundSql();
        final Executor executor = executorInvocation.getExecutor();
        final Tenant tenant=sqlContext.getRequest().getTenant();
        final Object parameter = executorInvocation.getParameter();
        final String tenantStatementId = this.getTenantStatementId(ms, tenant);
        try {
            SQLStatementInstrumentor instrumentor = SqlHelperMybatisPlugin.getInstrumentor();
           // String tenantSql = instrumentor.instrumentTenantSql(boundSql.getSql(), tenant);
           String tenantSql="UPDATE USER SET NAME = ?, AGE = ? WHERE TENANTID = ? AND ID = ? ";
            MappedStatement tenantStatement = this.customTenantStatement(ms, tenantStatementId);
            boundSql = MybatisUtils.rebuildBoundSql(tenantSql, tenantStatement.getConfiguration(), boundSql);
            executor.update(tenantStatement,parameter);
            executorInvocation.setBoundSql(boundSql);
            Pipelines.inbound(ctx);
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    private String getTenantStatementId(final MappedStatement ms, final Tenant tenant) {
        String tenantString = tenant.toString();
        StringBuilder builder = new StringBuilder(ms.getId() + "_");
        for (int i = 0; i < tenantString.length(); i++) {
            char c = tenantString.charAt(i);
            // 0-9, a-z, _
            if (Chars.isNumber(c) || Chars.isLowerCase(c) || Chars.isUpperCase(c) || c == '_') {
                builder.append(c);
            }
        }
        return builder.append(TENANT_SUFFIX).toString();
    }

    private MappedStatement customTenantStatement(final MappedStatement ms, final String tenantStatementId) {
        final MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), tenantStatementId, ms.getSqlSource(), ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (Emptys.isNotEmpty(ms.getKeyProperties())) {
            final StringBuilder keyProperties = new StringBuilder();
            for (final String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        final List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        final ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class, new ArrayList<ResultMapping>()).build();
        resultMaps.add(resultMap);
        builder.resultMaps(resultMaps);
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

}
