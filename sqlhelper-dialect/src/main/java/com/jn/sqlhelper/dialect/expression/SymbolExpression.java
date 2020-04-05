package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.expression.BaseExpression;
import com.jn.langx.util.Emptys;

public class SymbolExpression extends BaseExpression<SQLExpression> implements SQLExpression<SQLExpression> {
    private String databaseSymbol;

    public SymbolExpression() {
    }

    public SymbolExpression(String databaseSymbol) {
        setValue(databaseSymbol);
    }


    @Override
    public SQLExpression execute() {
        return this;
    }

    public void setValue(String databaseSymbol) {
        if (Emptys.isNotEmpty(databaseSymbol)) {
            this.databaseSymbol = databaseSymbol;
        }
    }

    public String getValue() {
        return this.databaseSymbol;
    }

    @Override
    public String toString() {
        return databaseSymbol;
    }
}
