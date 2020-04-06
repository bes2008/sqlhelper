package com.jn.sqlhelper.dialect.sqlparser.jsqlparser;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.instrument.InstrumentConfig;
import com.jn.sqlhelper.dialect.instrument.WhereInstrumentConfig;
import com.jn.sqlhelper.dialect.instrument.WhereInstrumentor;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import java.util.List;

public class JSqlParserWhereInstrumentor implements WhereInstrumentor<Statement> {

    @Override
    public void instrument(SqlStatementWrapper<Statement> statementWrapper, InstrumentConfig config) {
        if (Emptys.isEmpty(statementWrapper) || Emptys.isEmpty(config)) {
            return;
        }
        Statement statement = statementWrapper.get();
        List<WhereInstrumentConfig> expressionConfigs = config.getWhereInstrumentConfigs();
        if (Emptys.isEmpty(statement) || Emptys.isEmpty(expressionConfigs)) {
            return;
        }
        if (!JSqlParsers.isDML(statement)) {
            return;
        }

        if (Reflects.isSubClassOrEquals(Select.class, statement.getClass())) {
            instrument((Select)statement, expressionConfigs);
        } else if (Reflects.isSubClassOrEquals(Update.class, statement.getClass())) {
            instrument((Update)statement, expressionConfigs);
        } else if (Reflects.isSubClassOrEquals(Delete.class, statement.getClass())) {
            instrument((Delete)statement, expressionConfigs);
        }
    }

    private void instrument(Select select, List<WhereInstrumentConfig> expressionConfigs) {
        final PlainSelect plainSelect = JSqlParsers.extractPlainSelect(select.getSelectBody());
        if (plainSelect == null) {
            return;
        }

        Collects.forEach(expressionConfigs, new Consumer<WhereInstrumentConfig>() {
            @Override
            public void accept(WhereInstrumentConfig whereInstrumentConfig) {
                Expression where = plainSelect.getWhere();
                if(where==null){
                  //
                }
            }
        });
    }

    private void instrument(Update update, List<WhereInstrumentConfig> expressionConfigs) {

    }

    private void instrument(Delete delete, List<WhereInstrumentConfig> expressionConfigs) {

    }
}
