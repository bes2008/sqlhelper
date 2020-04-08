package com.jn.sqlhelper.jsqlparser.instrument;

import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.instrument.AbstractClauseTransformer;
import com.jn.sqlhelper.dialect.instrument.TransformConfig;
import com.jn.sqlhelper.dialect.instrument.where.WhereTransformConfig;
import com.jn.sqlhelper.dialect.instrument.where.WhereTransformer;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import com.jn.sqlhelper.jsqlparser.expression.ExpressionConverters;
import com.jn.sqlhelper.jsqlparser.utils.JSqlParsers;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import java.util.List;

public class JSqlParserWhereTransformer extends AbstractClauseTransformer<Statement> implements WhereTransformer<Statement> {

    @Override
    public void init() throws InitializationException {

    }

    @Override
    public SqlStatementWrapper<Statement> transform(SqlStatementWrapper<Statement> statementWrapper, TransformConfig config) {
        if (Emptys.isEmpty(statementWrapper) || Emptys.isEmpty(config)) {
            return statementWrapper;
        }
        Statement statement = statementWrapper.get();
        List<WhereTransformConfig> expressionConfigs = config.getWhereInstrumentConfigs();
        if (Emptys.isEmpty(statement) || Emptys.isEmpty(expressionConfigs)) {
            return statementWrapper;
        }
        if (!JSqlParsers.isDML(statement)) {
            return statementWrapper;
        }

        if (Reflects.isSubClassOrEquals(Select.class, statement.getClass())) {
            instrument((Select) statement, false, expressionConfigs);
        } else if (Reflects.isSubClassOrEquals(Update.class, statement.getClass())) {
            instrument((Update) statement, expressionConfigs);
        } else if (Reflects.isSubClassOrEquals(Delete.class, statement.getClass())) {
            instrument((Delete) statement, expressionConfigs);
        }
        return statementWrapper;
    }

    private void instrument(Select select, final boolean isSubSelect, List<WhereTransformConfig> expressionConfigs) {
        final PlainSelect plainSelect = JSqlParsers.extractPlainSelect(select.getSelectBody());
        if (plainSelect == null) {
            return;
        }

        Collects.forEach(expressionConfigs, new Predicate<WhereTransformConfig>() {
            @Override
            public boolean test(WhereTransformConfig config) {
                return config != null && config.getExpression() != null && (!isSubSelect || config.isInstrumentSubSelect());
            }
        }, new Consumer<WhereTransformConfig>() {
            @Override
            public void accept(WhereTransformConfig config) {
                Expression where = plainSelect.getWhere();
                Expression expression = ExpressionConverters.toJSqlParserExpression(config.getExpression());

                if (where == null) {
                    plainSelect.setWhere(expression);
                } else {
                    WhereTransformConfig.Position position = config.getPosition();
                    switch (position) {
                        case FIRST:
                            plainSelect.setWhere(new AndExpression(expression, where));
                            break;
                        case LAST:
                            plainSelect.setWhere(new AndExpression(where, expression));
                            break;
                        case BEST:
                            // TODO compute the best position based on the index
                            break;
                    }
                }
            }
        });
    }

    private void instrument(final Update update, List<WhereTransformConfig> expressionConfigs) {
        if ((update.isUseSelect() && update.getSelect() != null)) {
            instrument(update.getSelect(), true, expressionConfigs);
        }
        Collects.forEach(expressionConfigs, new Predicate<WhereTransformConfig>() {
            @Override
            public boolean test(WhereTransformConfig config) {
                return config != null && config.getExpression() != null;
            }
        }, new Consumer<WhereTransformConfig>() {
            @Override
            public void accept(WhereTransformConfig config) {
                Expression where = update.getWhere();
                Expression expression = ExpressionConverters.toJSqlParserExpression(config.getExpression());

                if (where == null) {
                    update.setWhere(expression);
                } else {
                    WhereTransformConfig.Position position = config.getPosition();
                    switch (position) {
                        case FIRST:
                            update.setWhere(new AndExpression(expression, where));
                            break;
                        case LAST:
                            update.setWhere(new AndExpression(where, expression));
                            break;
                        case BEST:
                            // TODO compute the best position based on the index
                            break;
                    }
                }
            }
        });


    }

    private void instrument(final Delete delete, List<WhereTransformConfig> expressionConfigs) {
        Collects.forEach(expressionConfigs, new Predicate<WhereTransformConfig>() {
            @Override
            public boolean test(WhereTransformConfig config) {
                return config != null && config.getExpression() != null;
            }
        }, new Consumer<WhereTransformConfig>() {
            @Override
            public void accept(WhereTransformConfig config) {
                Expression where = delete.getWhere();
                Expression expression = ExpressionConverters.toJSqlParserExpression(config.getExpression());

                if (where == null) {
                    delete.setWhere(expression);
                } else {
                    WhereTransformConfig.Position position = config.getPosition();
                    switch (position) {
                        case FIRST:
                            delete.setWhere(new AndExpression(expression, where));
                            break;
                        case LAST:
                            delete.setWhere(new AndExpression(where, expression));
                            break;
                        case BEST:
                            // TODO compute the best position based on the index
                            break;
                    }
                }
            }
        });
    }
}
