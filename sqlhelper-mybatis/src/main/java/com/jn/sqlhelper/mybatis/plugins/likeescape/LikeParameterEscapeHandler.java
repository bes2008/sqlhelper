/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.mybatis.plugins.likeescape;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.pipeline.AbstractHandler;
import com.jn.langx.pipeline.HandlerContext;
import com.jn.langx.pipeline.Pipelines;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objects;
import com.jn.langx.util.Strings;
import com.jn.langx.util.struct.Pair;
import com.jn.sqlhelper.common.formatter.SqlDmlFormatter;
import com.jn.sqlhelper.dialect.*;
import com.jn.sqlhelper.dialect.likeescaper.LikeEscaper;
import com.jn.sqlhelper.dialect.likeescaper.LikeEscapers;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import com.jn.sqlhelper.mybatis.plugins.ExecutorInvocation;
import com.jn.sqlhelper.mybatis.plugins.MybatisSqlRequestContextKeys;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * {@link org.apache.ibatis.executor.Executor#query(MappedStatement, Object, RowBounds, ResultHandler)}
 * {@link org.apache.ibatis.executor.Executor#query(MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)} )}
 */
public class LikeParameterEscapeHandler extends AbstractHandler {
    private static Logger logger = LoggerFactory.getLogger(LikeParameterEscapeHandler.class);

    @Override
    public void inbound(HandlerContext ctx) throws Throwable {
        ExecutorInvocation executorInvocation = (ExecutorInvocation) ctx.getPipeline().getTarget();
        MappedStatement mappedStatement = executorInvocation.getMappedStatement();

        if (!MybatisUtils.isQueryStatement(mappedStatement) || !MybatisUtils.isPreparedStatement(mappedStatement) || !isEnableLikeEscape()) {
            Pipelines.skipHandler(ctx, true);
            return;
        }

        // notice: the sqlcontext, sqlrequest is not null
        SqlRequestContext sqlContext = SqlRequestContextHolder.getInstance().get();
        LikeEscaper likeEscaper = getLikeEscaper(mappedStatement, sqlContext.getRequest());
        if (Objects.isNull(likeEscaper)) {
            logger.warn("Can't find a suitable LikeEscaper for the sql request: {}, statement id: {}", sqlContext.getRequest(), mappedStatement.getId());
            Pipelines.skipHandler(ctx, true);
            return;
        }
        sqlContext.getRequest().setLikeEscaper(likeEscaper);
        BoundSql boundSql = executorInvocation.getBoundSql();
        String sql = boundSql.getSql();
        Pair<List<Integer>, List<Integer>> pair = LikeEscapers.findEscapedSlots(sql);
        if (Emptys.isEmpty(pair.getKey()) && Emptys.isEmpty(pair.getValue())) {
            Pipelines.skipHandler(ctx, true);
            return;
        }

        String newSql = LikeEscapers.insertLikeEscapeDeclares(sql, pair.getValue(), likeEscaper);
        sqlContext.getRequest().setLikeParameterIndexes(pair.getKey());
        if (logger.isDebugEnabled()) {
            SqlDmlFormatter sqlDmlFormatter = new SqlDmlFormatter();
            logger.debug("After like escape, the sql {} become: {}", sqlDmlFormatter.format(sql), sqlDmlFormatter.format(newSql));
        }
        // rebuild a BoundSql
        boundSql = MybatisUtils.rebuildBoundSql(newSql, mappedStatement.getConfiguration(), boundSql);
        executorInvocation.setBoundSql(boundSql);
        sqlContext.set(MybatisSqlRequestContextKeys.LIKE_ESCAPE_PARAMETERS_INDEXES, pair.getKey());
        sqlContext.set(MybatisSqlRequestContextKeys.LIKE_ESCAPER, likeEscaper);
        Pipelines.inbound(ctx);
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
            SQLStatementInstrumentor instrumentor = SqlHelperMybatisPlugin.getInstrumentor();
            String databaseId = MybatisUtils.getDatabaseId(SqlRequestContextHolder.getInstance(), instrumentor, ms);
            if (Strings.isNotBlank(databaseId)) {
                likeEscaper = instrumentor.getDialectRegistry().getDialectByName(databaseId);
                if (likeEscaper != null && sqlRequest != null) {
                    sqlRequest.setDialect(databaseId);
                }
            }
        }
        return likeEscaper;
    }
}
