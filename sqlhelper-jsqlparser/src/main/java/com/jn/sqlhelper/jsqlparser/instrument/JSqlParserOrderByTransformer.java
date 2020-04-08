package com.jn.sqlhelper.jsqlparser.instrument;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.instrument.TransformConfig;
import com.jn.sqlhelper.dialect.instrument.orderby.OrderByTransformer;
import com.jn.sqlhelper.dialect.instrument.SQLTransformException;
import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.orderby.OrderByItem;
import com.jn.sqlhelper.dialect.orderby.OrderByType;
import com.jn.sqlhelper.dialect.sqlparser.SQLParseException;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import com.jn.sqlhelper.jsqlparser.utils.JSqlParsers;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

import java.util.ArrayList;
import java.util.List;

public class JSqlParserOrderByTransformer implements OrderByTransformer<Statement> {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isTransformable(SqlStatementWrapper<Statement> statementWrapper) {
        return false;
    }

    @Override
    public void init() throws InitializationException {

    }

    @Override
    public SqlStatementWrapper<Statement> transform(@NonNull SqlStatementWrapper<Statement> sw, @NonNull TransformConfig config) {
        Preconditions.checkNotNull(sw);
        Preconditions.checkNotNull(config);
        OrderBy orderBy = Preconditions.checkNotNull(config.getOrderBy());
        final Statement statement = sw.get();
        Preconditions.checkNotNull(statement,"statement is null");
        Preconditions.checkTrue(statement instanceof Select, new Supplier<Object[], String>() {
            @Override
            public String get(Object[] input) {
                return StringTemplates.formatWithPlaceholder("statement is not a select statement: {}", statement.toString());
            }
        });
        instrument((Select)statement,orderBy);
        return sw;
    }

    public static void instrument(@NonNull Select select, @NonNull OrderBy orderBy) throws SQLParseException {
        try {
            SelectBody selectBody = select.getSelectBody();

            PlainSelect plainSelect = JSqlParsers.extractPlainSelect(selectBody);
            if (plainSelect == null) {
                return;
            }
            List<OrderByElement> orderByElements = plainSelect.getOrderByElements();

            if (Emptys.isNotEmpty(orderByElements)) {
                String orderByStringInSql = PlainSelect.orderByToString(orderByElements);
                if (orderByStringInSql.contains("?")) {
                    throw new SQLTransformException("Can't instrument order by because the original sql has '?' in order by clause");
                }
            }

            if (orderBy.isValid()) {
                if (orderByElements == null) {
                    orderByElements = new ArrayList<OrderByElement>();
                }

                for (OrderByItem item : orderBy) {
                    Expression exprForAppend = CCJSqlParserUtil.parseExpression(item.getExpression());
                    boolean needAppend = true;
                    for (OrderByElement orderByElement : orderByElements) {
                        Expression exprInSql = orderByElement.getExpression();
                        if (exprForAppend.getClass() == exprInSql.getClass()) {
                            if (JSqlParsers.expressionEquals(exprForAppend, exprInSql)) {
                                needAppend = false;
                                // do asc, desc change
                                if (item.getType() == null) {
                                    orderByElement.setAscDescPresent(false);
                                } else {
                                    orderByElement.setAsc(item.getType() == OrderByType.ASC);
                                }
                            }
                        }

                    }

                    if (needAppend) {
                        OrderByElement orderByElement = new OrderByElement();
                        if (item.getType() == null) {
                            orderByElement.setAscDescPresent(false);
                        } else {
                            orderByElement.setAsc(item.getType() == OrderByType.ASC);
                        }
                        orderByElement.setExpression(exprForAppend);

                        orderByElements.add(orderByElement);
                    }
                }

                if (!orderByElements.isEmpty()) {
                    plainSelect.setOrderByElements(orderByElements);
                }
            }
        }catch (JSQLParserException ex){
            throw new SQLParseException(ex);
        }
    }
}
