package com.jn.sqlhelper.dialect.instrument;

import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

import java.util.HashMap;
import java.util.Map;

public class InstrumentedStatement implements SqlStatementWrapper {
    private String originalSql;
    private Map<InstrumentConfig, String> instrumentedSqlMap = new HashMap<InstrumentConfig, String>();
    private boolean changed =false;
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

    public void setCountSql(String countSql){
        InstrumentConfig config = new InstrumentConfig();
        config.setCount(true);
        instrumentedSqlMap.put(config, countSql);
    }

    public String getCountSql(){
        InstrumentConfig config = new InstrumentConfig();
        config.setCount(true);
        return instrumentedSqlMap.get(config);
    }

    public void setLimitSql(String dialect, String limitSql, boolean hasOffset){
        InstrumentConfig config = new InstrumentConfig();
        config.setDialect(dialect);
        config.setLimitOffset(hasOffset);
        instrumentedSqlMap.put(config, limitSql);
    }

    public String getLimitSql(String dialect, boolean hasOffset){
        InstrumentConfig config = new InstrumentConfig();
        config.setDialect(dialect);
        config.setLimitOffset(hasOffset);
        return instrumentedSqlMap.get(config);
    }

    public String getOrderBySql(OrderBy orderBy){
        InstrumentConfig config = new InstrumentConfig();
        config.setOrderBy(orderBy);
        return instrumentedSqlMap.get(config);
    }

    public void setOrderBySql(OrderBy orderBy, String orderBySql){
        InstrumentConfig config = new InstrumentConfig();
        config.setOrderBy(orderBy);
        instrumentedSqlMap.put(config, orderBySql);
    }

    public void setOrderByLimitSql(OrderBy orderBy, String dialect, String sql, boolean hasOffset){
        InstrumentConfig config = new InstrumentConfig();
        config.setOrderBy(orderBy);
        config.setDialect(dialect);
        config.setLimitOffset(hasOffset);
        instrumentedSqlMap.put(config, sql);
    }

    public String getOrderByLimitSql(OrderBy orderBy, String dialect, boolean hasOffset){
        InstrumentConfig config = new InstrumentConfig();
        config.setOrderBy(orderBy);
        config.setDialect(dialect);
        config.setLimitOffset(hasOffset);
        return instrumentedSqlMap.get(config);
    }

}
