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
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author huxiongming
 */
public class TenantHandler extends AbstractHandler{
    private static Logger logger = LoggerFactory.getLogger(TenantHandler.class);
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
        final Tenant tenant=sqlContext.getRequest().getTenant();
        try {
            String newSql=getTenantSql(boundSql,tenant);
            boundSql = MybatisUtils.rebuildBoundSql(newSql, ms.getConfiguration(), boundSql);
            executorInvocation.setBoundSql(boundSql);
            Pipelines.inbound(ctx);
        }catch (Throwable e){
            e.printStackTrace();
        }

    }


    private String getTenantSql(BoundSql boundSql,Tenant tenant) {
        SQLStatementInstrumentor instrumentor = SqlHelperMybatisPlugin.getInstrumentor();
        final String tenantSql = instrumentor.instrumentTenantSql(boundSql.getSql(), tenant);
        return tenantSql;
    }

}
