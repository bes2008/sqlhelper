package com.jn.sqlhelper.jsqlparser;

import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.instrument.InstrumentConfig;
import com.jn.sqlhelper.dialect.instrument.WhereInstrumentConfig;
import com.jn.sqlhelper.dialect.instrument.WhereInstrumentor;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import com.jn.sqlhelper.dialect.sqlparser.jsqlparser.expression.ExpressionConverters;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
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
            instrument((Select) statement,false, expressionConfigs);
        } else if (Reflects.isSubClassOrEquals(Update.class, statement.getClass())) {
            instrument((Update) statement, expressionConfigs);
        } else if (Reflects.isSubClassOrEquals(Delete.class, statement.getClass())) {
            instrument((Delete) statement, expressionConfigs);
        }
    }

    private void instrument(Select select, final boolean isSubSelect, List<WhereInstrumentConfig> expressionConfigs) {
        final PlainSelect plainSelect = JSqlParsers.extractPlainSelect(select.getSelectBody());
        if (plainSelect == null) {
            return;
        }

        Collects.forEach(expressionConfigs, new Predicate<WhereInstrumentConfig>() {
            @Override
            public boolean test(WhereInstrumentConfig config) {
                return config != null && config.getExpression() != null && (!isSubSelect || config.isInstrumentSubSelect());
            }
        }, new Consumer<WhereInstrumentConfig>() {
            @Override
            public void accept(WhereInstrumentConfig config) {
                Expression where = plainSelect.getWhere();
                Expression expression = ExpressionConverters.toJSqlParserExpression(config.getExpression());

                if (where == null) {
                    plainSelect.setWhere(expression);
                } else {
                    WhereInstrumentConfig.Position position = config.getPosition();
                    switch (position) {
                        case Position.FIRST:
                            plainSelect.setWhere(new AndExpression(expression, where));
                            break;
                        case Position.LAST:
                            plainSelect.setWhere(new AndExpression(where, expression));
                            break;
                        case Position.BEST:
                            // TODO compute the best position based on the index
                            break;
                    }
                }
            }
        });
    }

    private void instrument(final Update update, List<WhereInstrumentConfig> expressionConfigs) {
        if((update.isUseSelect() && update.getSelect()!=null)){
            instrument(update.getSelect(),true, expressionConfigs);
        }
        Collects.forEach(expressionConfigs, new Predicate<WhereInstrumentConfig>() {
            @Override
            public boolean test(WhereInstrumentConfig config) {
                return config != null && config.getExpression() != null;
            }
        }, new Consumer<WhereInstrumentConfig>() {
            @Override
            public void accept(WhereInstrumentConfig config) {
                Expression where = update.getWhere();
                Expression expression = ExpressionConverters.toJSqlParserExpression(config.getExpression());

                if (where == null) {
                    update.setWhere(expression);
                } else {
                    WhereInstrumentConfig.Position position = config.getPosition();
                    switch (position) {
                        case Position.FIRST:
                            update.setWhere(new AndExpression(expression, where));
                            break;
                        case Position.LAST:
                            update.setWhere(new AndExpression(where, expression));
                            break;
                        case Position.BEST:
                            // TODO compute the best position based on the index
                            break;
                    }
                }
            }
        });


    }

    private void instrument(final Delete delete, List<WhereInstrumentConfig> expressionConfigs) {
        Collects.forEach(expressionConfigs, new Predicate<WhereInstrumentConfig>() {
            @Override
            public boolean test(WhereInstrumentConfig config) {
                return config != null && config.getExpression() != null;
            }
        }, new Consumer<WhereInstrumentConfig>() {
            @Override
            public void accept(WhereInstrumentConfig config) {
                Expression where = delete.getWhere();
                Expression expression = ExpressionConverters.toJSqlParserExpression(config.getExpression());

                if (where == null) {
                    delete.setWhere(expression);
                } else {
                    WhereInstrumentConfig.Position position = config.getPosition();
                    switch (position) {
                        case Position.FIRST:
                            delete.setWhere(new AndExpression(expression, where));
                            break;
                        case Position.LAST:
                            delete.setWhere(new AndExpression(where, expression));
                            break;
                        case Position.BEST:
                            // TODO compute the best position based on the index
                            break;
                    }
                }
            }
        });
    }
}
