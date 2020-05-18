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
import com.jn.sqlhelper.dialect.instrument.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.SqlRequest;
import com.jn.sqlhelper.dialect.SqlRequestContext;
import com.jn.sqlhelper.dialect.SqlRequestContextHolder;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Map;

public class MybatisUtils {
    private static VendorDatabaseIdProvider vendorDatabaseIdProvider;

    static {
        vendorDatabaseIdProvider = new CustomVendorDatabaseIdProvider();
    }

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

    public static String getDatabaseId(@Nullable SqlRequestContextHolder sqlRequestContextHolder,
                                       @Nullable SQLStatementInstrumentor instrumentor,
                                       @NonNull final MappedStatement ms) {
        String databaseId = null;
        if (sqlRequestContextHolder != null) {
            SqlRequestContext sqlRequestContext = sqlRequestContextHolder.get();
            if (sqlRequestContext != null) {
                SqlRequest request = sqlRequestContext.getRequest();
                if (request != null) {
                    databaseId = request.getDialect();
                }
            }
        }

        if (Emptys.isEmpty(databaseId)) {
            databaseId = ms.getDatabaseId();
        }

        if (Emptys.isEmpty(databaseId) && instrumentor != null && instrumentor.getConfig() != null) {
            databaseId = instrumentor.getConfig().getDialect();
        }

        if (Emptys.isEmpty(databaseId)) {
            return ms.getConfiguration().getDatabaseId();
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
}
