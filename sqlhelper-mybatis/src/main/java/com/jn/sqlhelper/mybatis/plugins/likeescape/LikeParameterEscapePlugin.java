package com.jn.sqlhelper.mybatis.plugins.likeescape;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Strings;
import com.jn.sqlhelper.dialect.*;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "queryCursor", args = {MappedStatement.class, Object.class, RowBounds.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class LikeParameterEscapePlugin implements Interceptor, Initializable {
    private static final Logger logger = LoggerFactory.getLogger(LikeParameterEscapePlugin.class);
    private static final SQLStatementInstrumentor instrumentor = new SQLStatementInstrumentor();

    @Override
    public void init() throws InitializationException {

    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (logger.isDebugEnabled()) {
            logger.debug("{}", invocation);
        }
        final Object[] args = invocation.getArgs();
        final MappedStatement ms = (MappedStatement) args[0];
        if (!MybatisUtils.isPreparedStatement(ms)) {
            return invocation.proceed();
        }
        if (!isEnableLikeEscape()) {
            return invocation.proceed();
        }

        final Object parameter = args[1];
        final Executor executor = (Executor) invocation.getTarget();

        BoundSql boundSql = null;
        CacheKey cacheKey = null;
        ResultHandler resultHandler = null;
        if (args.length >= 4) {
            final RowBounds rowBounds = (RowBounds) args[2];
            resultHandler = (ResultHandler) args[3];
            if (args.length == 4) {
                boundSql = ms.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            } else {
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }
        } else {
            boundSql = ms.getBoundSql(parameter);
        }

        String sql = boundSql.getSql();
        SqlRequestContext sqlContext = SqlRequestContextHolder.getInstance().get();
        LikeEscaper likeEscaper = getLikeEscaper(ms, sqlContext.getRequest());
        return invocation.proceed();
    }

    private boolean isEnableLikeEscape() {
        SqlRequestContext sqlContext = SqlRequestContextHolder.getInstance().get();
        if (sqlContext == null) {
            // using global configuration
            return false;
        } else {
            SqlRequest sqlRequest = sqlContext.getRequest();
            return sqlRequest != null && sqlRequest.isEscapeLikeParameter();
        }
    }

    private LikeEscaper getLikeEscaper(@NonNull MappedStatement ms, @Nullable SqlRequest sqlRequest) {
        LikeEscaper likeEscaper = null;
        if (sqlRequest != null) {
            likeEscaper = sqlRequest.getLikeEscaper();
        }
        if (likeEscaper == null) {
            String databaseId = MybatisUtils.getDatabaseId(SqlRequestContextHolder.getInstance(), instrumentor, ms);
            if (Strings.isNotBlank(databaseId)) {
                likeEscaper = instrumentor.getDialectRegistry().getDialectByName(databaseId);
            }
        }
        return likeEscaper;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            if (logger.isDebugEnabled()) {
                logger.debug("wrap mybatis executor {}", target.getClass());
            }
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
