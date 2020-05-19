package com.jn.sqlhelper.dialect.tenant;

import java.util.List;

/**
 * @author huxiongming
 */
public class Tenant {

    /**
     * 表名
     */
    private String table;

    /**
     * 租户ID的列名
     */
    private String column;

    /**
     * 操作符
     */
    private String operateSymbol;

    /**
     * 参数值
     */
    private List values;

    /**
     * 是否为 not 表达式
     */
    private boolean not;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getOperateSymbol() {
        return operateSymbol;
    }

    public void setOperateSymbol(String operateSymbol) {
        this.operateSymbol = operateSymbol;
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    @Override
    public String toString() {
        return "_tenant";
    }
}
