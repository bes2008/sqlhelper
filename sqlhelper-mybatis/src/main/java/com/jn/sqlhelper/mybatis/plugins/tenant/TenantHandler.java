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
import com.jn.sqlhelper.mybatis.plugins.ExecutorInvocation;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huxiongming
 */
public class TenantHandler extends AbstractHandler{
    private static Logger logger = LoggerFactory.getLogger(TenantHandler.class);
    private static final String TENANT_SUFFIX = "tenant";

    @Override
    public void inbound(HandlerContext ctx) throws Throwable {
        SqlRequestContext sqlContext = SqlRequestContextHolder.getInstance().get();
        ExecutorInvocation executorInvocation = (ExecutorInvocation) ctx.getPipeline().getTarget();
        MappedStatement mappedStatement = executorInvocation.getMappedStatement();
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
        SQLStatementInstrumentor instrumentor = SqlHelperMybatisPlugin.getInstrumentor();
        try {
            String tenantSql = instrumentor.instrumentTenantSql(boundSql.getSql(), tenant);
            if(SqlCommandType.SELECT.equals(ms.getSqlCommandType())){
               boundSql = MybatisUtils.rebuildBoundSql(tenantSql, ms.getConfiguration(), boundSql);
               executorInvocation.setBoundSql(boundSql);
               Pipelines.inbound(ctx);
            }else{
                boundSql = MybatisUtils.rebuildBoundSql(tenantSql, ms.getConfiguration(), boundSql);
                String tenantStatementId=this.getTenantStatementId(ms,tenant);
                MappedStatement customTenantStatement=this.customTenantStatement(ms,parameter,tenantStatementId,boundSql);
                executorInvocation.setResult(executor.update(customTenantStatement, parameter));
                return;
            }

        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            instrumentor.finish();
        }
    }
    private String getTenantStatementId(final MappedStatement ms, final Tenant tenant) {
        StringBuilder builder = new StringBuilder(ms.getId() + "_");
        return builder.append(TENANT_SUFFIX).toString();
    }
    private MappedStatement customTenantStatement(final MappedStatement ms, Object parameter,final String tenantStatementId,BoundSql boundSql) {
        List<ParameterMapping> parameterMappings= ms.getBoundSql(ms.getParameterMap().getType()).getParameterMappings();
        StaticSqlSource sqlSource=new StaticSqlSource(ms.getConfiguration(), boundSql.getSql(), parameterMappings);
        final MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), tenantStatementId, sqlSource, ms.getSqlCommandType());
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
