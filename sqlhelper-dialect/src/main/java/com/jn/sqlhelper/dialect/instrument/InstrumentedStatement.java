package com.jn.sqlhelper.dialect.instrument;

import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;

import java.util.HashMap;
import java.util.Map;

public class InstrumentedStatement implements SqlStatementWrapper {
    private String originalSql;
    private Map<InstrumentCondition, String> instrumentedSqlMap = new HashMap<InstrumentCondition, String>();

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

    public void setCountSql(String countSql){
        InstrumentCondition condition = new InstrumentCondition();
        condition.setCount(true);
        instrumentedSqlMap.put(condition, countSql);
    }

    public String getCountSql(){
        InstrumentCondition condition = new InstrumentCondition();
        condition.setCount(true);
        return instrumentedSqlMap.get(condition);
    }

    public void setLimitSql(String dialect, String limitSql, boolean hasOffset){
        InstrumentCondition condition = new InstrumentCondition();
        condition.setDialect(dialect);
        condition.setLimitOffset(hasOffset);
        instrumentedSqlMap.put(condition, limitSql);
    }

    public String getLimitSql(String dialect, boolean hasOffset){
        InstrumentCondition condition = new InstrumentCondition();
        condition.setDialect(dialect);
        condition.setLimitOffset(hasOffset);
        return instrumentedSqlMap.get(condition);
    }

    public String getOrderBySql(OrderBy orderBy){
        InstrumentCondition condition = new InstrumentCondition();
        condition.setOrderBy(orderBy);
        return instrumentedSqlMap.get(condition);
    }

    public void setOrderBySql(OrderBy orderBy, String orderBySql){
        InstrumentCondition condition = new InstrumentCondition();
        condition.setOrderBy(orderBy);
        instrumentedSqlMap.put(condition, orderBySql);
    }

    public void setOrderByLimitSql(OrderBy orderBy, String dialect, String sql, boolean hasOffset){
        InstrumentCondition condition = new InstrumentCondition();
        condition.setOrderBy(orderBy);
        condition.setDialect(dialect);
        condition.setLimitOffset(hasOffset);
        instrumentedSqlMap.put(condition, sql);
    }

    public String getOrderByLimitSql(OrderBy orderBy, String dialect, boolean hasOffset){
        InstrumentCondition condition = new InstrumentCondition();
        condition.setOrderBy(orderBy);
        condition.setDialect(dialect);
        condition.setLimitOffset(hasOffset);
        return instrumentedSqlMap.get(condition);
    }

}
