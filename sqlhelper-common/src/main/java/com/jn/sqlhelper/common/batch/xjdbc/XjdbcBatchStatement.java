package com.jn.sqlhelper.common.batch.xjdbc;

import com.jn.sqlhelper.common.batch.BatchMode;
import com.jn.sqlhelper.common.batch.BatchStatement;

public class XjdbcBatchStatement implements BatchStatement {
    private BatchMode batchMode;
    private String sql;

    public XjdbcBatchStatement(String sql) {
        this(sql, null);
    }

    public XjdbcBatchStatement(String sql, BatchMode batchMode) {
        if (batchMode == null) {
            batchMode = BatchMode.JDBC_BATCH;
        }
        this.batchMode = batchMode;
        this.sql = sql;
    }

    @Override
    public BatchMode getBatchMode() {
        return batchMode;
    }

    @Override
    public void setBatchMode(BatchMode batchMode) {
        this.batchMode = batchMode;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
