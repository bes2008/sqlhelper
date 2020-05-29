package com.jn.sqlhelper.dialect.instrument;

import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import com.jn.sqlhelper.dialect.tenant.Tenant;

import java.util.HashMap;
import java.util.Map;

public class InstrumentedStatement implements SqlStatementWrapper {
    private String originalSql;
    private Map<TransformConfig, String> instrumentedSqlMap = new HashMap<TransformConfig, String>();
    private boolean changed = false;

    @Override
    public String getOriginalSql() {
        return originalSql;
    }

    @Override
    public void setOriginalSql(String sql) {
        this.originalSql = sql;
    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public boolean isChanged() {
        return this.changed;
    }

    @Override
    public String getSql() {
        return null;
    }

    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void setCountSql(String countSql) {
        TransformConfig config = new TransformConfig();
        config.setCount(true);
        instrumentedSqlMap.put(config, countSql);
    }

    public String getCountSql() {
        TransformConfig config = new TransformConfig();
        config.setCount(true);
        return instrumentedSqlMap.get(config);
    }

    public void setLimitSql(String dialect, String limitSql, boolean hasOffset) {
        TransformConfig config = new TransformConfig();
        config.setDialect(dialect);
        config.setLimitOffset(hasOffset);
        instrumentedSqlMap.put(config, limitSql);
    }

    public String getLimitSql(String dialect, boolean hasOffset) {
        TransformConfig config = new TransformConfig();
        config.setDialect(dialect);
        config.setLimitOffset(hasOffset);
        return instrumentedSqlMap.get(config);
    }

    public String getOrderBySql(OrderBy orderBy) {
        TransformConfig config = new TransformConfig();
        config.setOrderBy(orderBy);
        return instrumentedSqlMap.get(config);
    }

    public void setOrderBySql(OrderBy orderBy, String orderBySql) {
        TransformConfig config = new TransformConfig();
        config.setOrderBy(orderBy);
        instrumentedSqlMap.put(config, orderBySql);
    }


    public void setOrderByLimitSql(OrderBy orderBy, String dialect, String sql, boolean hasOffset) {
        TransformConfig config = new TransformConfig();
        config.setOrderBy(orderBy);
        config.setDialect(dialect);
        config.setLimitOffset(hasOffset);
        instrumentedSqlMap.put(config, sql);
    }

    public String getOrderByLimitSql(OrderBy orderBy, String dialect, boolean hasOffset) {
        TransformConfig config = new TransformConfig();
        config.setOrderBy(orderBy);
        config.setDialect(dialect);
        config.setLimitOffset(hasOffset);
        return instrumentedSqlMap.get(config);
    }

    @Override
    public void setStatement(Object o) {

    }


    public void setInstrumentedSql(TransformConfig config, String newSql){
        this.instrumentedSqlMap.put(config, newSql);
    }


    public String getInstrumentedSql(TransformConfig config){
        return this.instrumentedSqlMap.get(config);
    }

}
