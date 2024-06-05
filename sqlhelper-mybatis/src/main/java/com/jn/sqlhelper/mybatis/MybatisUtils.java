/*
 * Copyright 2019 the original author or authors.
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

package com.jn.sqlhelper.mybatis;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.SqlRequest;
import com.jn.sqlhelper.dialect.SqlRequestContext;
import com.jn.sqlhelper.dialect.SqlRequestContextHolder;
import com.jn.sqlhelper.dialect.instrument.SQLStatementInstrumentor;
import com.jn.sqlhelper.mybatis.plugins.SqlHelperMybatisPlugin;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Map;

public class MybatisUtils {
    private static final Logger logger = LoggerFactory.getLogger(MybatisUtils.class);

    private static VendorDatabaseIdProvider vendorDatabaseIdProvider;

    static {
        vendorDatabaseIdProvider = new CustomVendorDatabaseIdProvider();
    }
    private MybatisUtils(){}
    public static VendorDatabaseIdProvider vendorDatabaseIdProvider() {
        return vendorDatabaseIdProvider;
    }

    public static boolean isPagingRowBounds(RowBounds rowBounds) {
        if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            return false;
        }
        return rowBounds.getOffset() != RowBounds.NO_ROW_OFFSET || rowBounds.getLimit() != RowBounds.NO_ROW_LIMIT;
    }

    public static boolean hasStatement(@NonNull SqlSessionFactory sessionFactory, String statementName) {
        return sessionFactory.getConfiguration().hasStatement(statementName);
    }

    public static boolean isQueryStatement(@NonNull final MappedStatement statement) {
        return SqlCommandType.SELECT == statement.getSqlCommandType();
    }

    public static boolean isPreparedStatement(@NonNull final MappedStatement statement) {
        return statement.getStatementType() == StatementType.PREPARED || statement.getStatementType() == StatementType.CALLABLE;
    }

    public static boolean isCallableStatement(@NonNull final MappedStatement statement) {
        return statement.getStatementType() == StatementType.CALLABLE;
    }

    /**
     * @deprecated 
     * @see #getDatabaseId(SqlRequestContextHolder, SQLStatementInstrumentor, MappedStatement, Executor, boolean)
     */
    @Deprecated
    public static String getDatabaseId(@Nullable SqlRequestContextHolder sqlRequestContextHolder,
                                       @Nullable SQLStatementInstrumentor instrumentor,
                                       @NonNull final MappedStatement ms, Executor executor) {
        return getDatabaseId(sqlRequestContextHolder, instrumentor, ms, executor, true);
    }

    public static String getDatabaseId(@Nullable SqlRequestContextHolder sqlRequestContextHolder,
                                       @Nullable SQLStatementInstrumentor instrumentor,
                                       @NonNull final MappedStatement ms,
                                       @Nullable Executor executor,
                                       boolean enableExtractDialectUseNative) {
        String databaseId = null;
        // 从 sql request 中获取
        if (sqlRequestContextHolder != null) {
            SqlRequestContext sqlRequestContext = sqlRequestContextHolder.get();
            if (sqlRequestContext != null) {
                SqlRequest request = sqlRequestContext.getRequest();
                if (request != null) {
                    databaseId = request.getDialect();
                }
            }
        }

        // 从 mapped statement 中获取
        if (Emptys.isEmpty(databaseId)) {
            databaseId = ms.getDatabaseId();
        }

        // 从 全局配置 中获取
        if (Emptys.isEmpty(databaseId) && instrumentor != null && instrumentor.getConfig() != null) {
            databaseId = instrumentor.getConfig().getDialect();
        }

        // 从 MyBatis Configuration 中获取
        if (Emptys.isEmpty(databaseId) && enableExtractDialectUseNative) {
            databaseId = ms.getConfiguration().getDatabaseId();
        }

        // 从连接的数据库中获取
        if (Emptys.isEmpty(databaseId) && executor != null) {
            Transaction tx = executor.getTransaction();
            try {
                Connection connection = tx.getConnection();
                if(instrumentor==null){
                    instrumentor= SqlHelperMybatisPlugin.getInstrumentor();
                }
                Dialect dialect = instrumentor.getDialect(connection.getMetaData());
                return dialect.getDatabaseId();
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return databaseId;
    }


    public static BoundSql rebuildBoundSql(String newSql, Configuration configuration, BoundSql boundSql) {
        BoundSql newBoundSql = new BoundSql(configuration, newSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        final Map<String, Object> additionalParameters = BoundSqls.getAdditionalParameter(boundSql);
        for (Map.Entry<String, Object> entry : additionalParameters.entrySet()) {
            newBoundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
        }
        return newBoundSql;
    }

    public static String getSql(BoundSql boundSql) {
        return boundSql.getSql();
    }

    public static boolean hasInterceptor(@NonNull Configuration configuration,@NonNull Interceptor interceptor){
        return configuration.getInterceptors().contains(interceptor);
    }

    public static boolean hasStatement(SqlSession session, String statementId){
        Configuration configuration = session.getConfiguration();
        return configuration.hasStatement(statementId);
    }
}
