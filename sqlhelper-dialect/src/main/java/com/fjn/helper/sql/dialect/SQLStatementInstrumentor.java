
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

package com.fjn.helper.sql.dialect;

import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SQLStatementInstrumentor {
    private static final Logger logger;
    private SQLInstrumentConfig config;
    private DialectRegistry dialectRegistry;
    private static final ThreadLocal<Dialect> DIALECT_HOLDER;

    public SQLStatementInstrumentor() {
        this.dialectRegistry = DialectRegistry.getInstance();
    }

    public boolean beginIfSupportsLimit(final Statement statement) {
        final Dialect dialect = this.getDialect(statement);
        return this.beginIfSupportsLimit(dialect);
    }

    public boolean beginIfSupportsLimit(final String databaseId) {
        final Dialect dialect = this.dialectRegistry.getDialectByName(databaseId);
        return this.beginIfSupportsLimit(dialect);
    }

    public boolean beginIfSupportsLimit(final DatabaseMetaData databaseMetaData) {
        final Dialect dialect = this.dialectRegistry.getDialectByDatabaseMetadata(databaseMetaData);
        return this.beginIfSupportsLimit(dialect);
    }

    private boolean beginIfSupportsLimit(final Dialect dialect) {
        if (dialect == null) {
            return false;
        }
        final boolean supports = dialect.isSupportsLimit();
        if (supports) {
            SQLStatementInstrumentor.DIALECT_HOLDER.set(dialect);
        }
        return supports;
    }

    private Dialect getCurrentDialect() {
        return SQLStatementInstrumentor.DIALECT_HOLDER.get();
    }

    private Dialect getDialect(final Statement statement) {
        Dialect dialect = this.getCurrentDialect();
        if (dialect != null) {
            return dialect;
        }
        final String dialectName = this.config.getDialect();
        if (dialectName != null) {
            dialect = this.dialectRegistry.getDialectByName(dialectName);
        }
        if (dialect == null && this.config.getDialectClassName() != null) {
            dialect = this.dialectRegistry.getDialectByClassName(this.config.getDialectClassName());
        }
        if (dialect == null && statement != null) {
            try {
                final Connection connection = statement.getConnection();
                dialect = this.dialectRegistry.getDialectByDatabaseMetadata(connection.getMetaData());
            } catch (SQLException e) {
                SQLStatementInstrumentor.logger.error("sql error code: {}, message: {}", new Object[]{Integer.valueOf(e.getErrorCode()), e.getMessage(), e});
            }
        }
        return dialect;
    }

    public String instrumentSql(String sql, final RowSelection selection) {
        final Dialect dialect = this.getCurrentDialect();
        if (LimitHelper.useLimit(dialect, selection)) {
            sql = dialect.getLimitSql(sql, selection);
        }
        return sql;
    }

    public void finish() {
        SQLStatementInstrumentor.DIALECT_HOLDER.remove();
    }

    public String countSql(String originalSql) {
        boolean hasOrderBy = false;
        final String lowerSql = originalSql.toLowerCase().trim();
        final int orderIndex = originalSql.toLowerCase().lastIndexOf("order");
        if (orderIndex != -1) {
            final String remainSql = lowerSql.substring(orderIndex + "order".length()).trim();
            hasOrderBy = remainSql.startsWith("by");
            if ((remainSql.contains("select") && remainSql.contains("from")) || remainSql.contains(" union ")) {
                hasOrderBy = false;
            }
        }
        if (hasOrderBy) {
            originalSql = originalSql.trim().substring(0, orderIndex).trim();
        }
        return "select count(0) from (" + originalSql + ") tmp_count";
    }

    public PreparedStatement bindParameters(final PreparedStatement statement, final PrepareParameterSetter parameterSetter, final QueryParameters queryParameters, final boolean setOriginalParameters) throws SQLException, SQLDialectException {
        final Dialect dialect = this.getDialect(statement);
        final RowSelection selection = queryParameters.getRowSelection();
        final boolean callable = queryParameters.isCallable();
        try {
            int col = 1;
            col += dialect.bindLimitParametersAtStartOfQuery(selection, statement, col);
            if (callable) {
                col = dialect.registerResultSetOutParameter((CallableStatement) statement, col);
            }
            if (setOriginalParameters) {
                col += parameterSetter.setParameters(statement, queryParameters, col);
            }
            col += dialect.bindLimitParametersAtEndOfQuery(selection, statement, col);
            dialect.setMaxRows(selection, statement);
            if (selection != null) {
                if (selection.getTimeout() != null && selection.getTimeout() > 0) {
                    statement.setQueryTimeout(selection.getTimeout());
                }
                if (selection.getFetchSize() != null && selection.getFetchSize() > 0) {
                    statement.setFetchSize(selection.getFetchSize());
                }
            }
        } catch (SQLException ex) {
            SQLStatementInstrumentor.logger.error("Set sql parameter fail, errorCode: {}, stack:{}", (Object) Integer.valueOf(ex.getErrorCode()), (Object) ex);
        }
        return statement;
    }

    public SQLInstrumentConfig getConfig() {
        return this.config;
    }

    public void setConfig(final SQLInstrumentConfig config) {
        this.config = config;
    }

    public void setDialectRegistry(final DialectRegistry dialectRegistry) {
        this.dialectRegistry = dialectRegistry;
    }

    static {
        logger = LoggerFactory.getLogger((Class) SQLStatementInstrumentor.class);
        DIALECT_HOLDER = new ThreadLocal<Dialect>();
    }
}
