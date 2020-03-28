package com.jn.sqlhelper.ebean;

import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.SQLStatementInstrumentor;
import com.jn.sqlhelper.dialect.conf.SQLInstrumentConfig;
import com.jn.sqlhelper.dialect.internal.AbstractDialect;
import com.jn.sqlhelper.dialect.internal.limit.OracleXLimitHandler;
import io.ebean.config.dbplatform.*;

public class EBeanCommonSqlLimiter implements SqlLimiter {
    private SQLStatementInstrumentor instrumentor;
    private SqlLimiter ebeanDefaultSqlLimiter = new LimitOffsetSqlLimiter();

    public SQLStatementInstrumentor getInstrumentor() {
        return instrumentor;
    }

    public void setInstrumentor(SQLStatementInstrumentor instrumentor) {
        this.instrumentor = instrumentor;
    }

    private String findDatabaseId(DatabasePlatform databasePlatform) {
        String databaseId = DialectRegistry.guessDatabaseId(databasePlatform.getPlatform().name());
        if (databaseId != null) {
            if (instrumentor.getConfig() == null) {
                instrumentor.setConfig(new SQLInstrumentConfig());
            }
            instrumentor.getConfig().setDialect(databaseId);
        }
        if (databaseId == null) {
            if (instrumentor.getConfig() != null) {
                return instrumentor.getConfig().getDialect();
            }
        }
        return databaseId;
    }

    @Override
    public SqlLimitResponse limit(SqlLimitRequest request) {
        String databaseId = findDatabaseId(request.getDbPlatform());
        if (databaseId == null || !instrumentor.beginIfSupportsLimit(databaseId)) {
            return ebeanDefaultSqlLimiter.limit(request);
        }
        Dialect dialect = DialectRegistry.getInstance().getDialectByName(databaseId);
        dialect.setUseLimitInVariableMode(false);
        String dbSql = request.getDbSql();

        StringBuilder sb = new StringBuilder(50 + dbSql.length());
        sb.append("select ");
        if (dialect.isSupportsDistinct() && request.isDistinct()) {
            sb.append("distinct ");
        }
        sb.append(dbSql);

        RowSelection rowSelection = new RowSelection();
        rowSelection.setOffset(Long.valueOf(request.getFirstRow()));
        rowSelection.setLimit(request.getMaxRows());
        String sql = instrumentor.instrumentLimitSql(dialect, sb.toString(), rowSelection);
        sql = request.getDbPlatform().completeSql(sql, request.getOrmQuery());

        boolean needRowNo = false;
        if (dialect instanceof AbstractDialect) {
            needRowNo = ((AbstractDialect) dialect).getLimitHandler() instanceof OracleXLimitHandler;
        }
        return new SqlLimitResponse(sql, needRowNo);
    }
}
