package com.jn.sqlhelper.jsqlparser.instrument;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.dialect.instrument.AbstractClauseTransformer;
import com.jn.sqlhelper.dialect.instrument.TransformConfig;
import com.jn.sqlhelper.dialect.instrument.tenant.TenantTransformer;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import com.jn.sqlhelper.dialect.tenant.Tenant;
import com.jn.sqlhelper.jsqlparser.utils.JSqlParsers;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huxiongming
 */
public class JSqlParserTenantTransformer extends AbstractClauseTransformer<Statement> implements TenantTransformer<Statement> {
    private static final Logger logger = LoggerFactory.getLogger(JSqlParserTenantTransformer.class);

    @Override
    public void init() throws InitializationException {

    }
    @Override
    public SqlStatementWrapper<Statement> transform(@NonNull SqlStatementWrapper<Statement> statementWrapper, @NonNull TransformConfig config) {
        Preconditions.checkNotNull(statementWrapper);
        Preconditions.checkNotNull(config);
        Tenant tenantConfig = config.getTenant();
        try {
            Statement  statement = CCJSqlParserUtil.parse(statementWrapper.getSql());
            if (Emptys.isEmpty(statement)) {
                return statementWrapper;
            }
            if (!JSqlParsers.isDML(statement)) {
                return statementWrapper;
            }
            if (Reflects.isSubClassOrEquals(Select.class, statement.getClass())) {
                transform((Select) statement,tenantConfig);
            } else if (Reflects.isSubClassOrEquals(Update.class, statement.getClass())) {
                transform((Update) statement,tenantConfig);
            } else if (Reflects.isSubClassOrEquals(Delete.class, statement.getClass())) {
                transform((Delete) statement,tenantConfig);
            }
            else if (Reflects.isSubClassOrEquals(Insert.class, statement.getClass())) {
                transform((Insert) statement,tenantConfig);
            }
            statementWrapper.setStatement(statement);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return statementWrapper;
    }

    private void transform(Select select, Tenant tenantConfig) {
        SelectBody selectBody = select.getSelectBody();
        final PlainSelect plainSelect = JSqlParsers.extractPlainSelect(selectBody);
        if (plainSelect == null) {
            return;
        }
        Expression where = plainSelect.getWhere();
        plainSelect.setWhere(builderExpression(where,tenantConfig));
    }

    protected Expression builderExpression(Expression currentExpression,Tenant tenantConfig) {
        if(Emptys.isNotEmpty(tenantConfig)){
            final Expression tenantExpression = tenantConfig.getTenant(false);
            Expression appendExpression;
            if (!(tenantExpression instanceof SupportsOldOracleJoinSyntax)) {
                appendExpression = new EqualsTo();
                ((EqualsTo) appendExpression).setLeftExpression(new Column(tenantConfig.getTenantColumn()));
                ((EqualsTo) appendExpression).setRightExpression(tenantExpression);
            } else {
                appendExpression = tenantExpression;
            }
            if (currentExpression == null) {
                return appendExpression;
            }
            return new AndExpression(currentExpression, appendExpression);
        }
       return currentExpression;
    }

    private void  transform(Update update,Tenant tenantConfig){
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(tenantConfig.getTenantColumn()));
        equalsTo.setRightExpression(tenantConfig.getTenant(true));
        update.setWhere(andExpression(equalsTo,update.getWhere()));
    }

    private void  transform(Delete delete,Tenant tenantConfig){
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(tenantConfig.getTenantColumn()));
        equalsTo.setRightExpression(tenantConfig.getTenant(true));
        delete.setWhere(andExpression(equalsTo,delete.getWhere()));
    }

    private void  transform(Insert insert, Tenant tenantConfig) {
        insert.getColumns().add(new Column(tenantConfig.getTenantColumn()));
        if (insert.getItemsList() != null) {
            // fixed github pull/295
            ItemsList itemsList = insert.getItemsList();
            if (itemsList instanceof ExpressionList) {
                ((ExpressionList) insert.getItemsList()).getExpressions().add(new StringValue(tenantConfig.getSingleTenantValues()));
            }
        }
    }


    /**
     * delete update 语句 where 处理
     */
    protected Expression andExpression(Expression tenantException, Expression where) {
        if (null != where) {
            if (where instanceof OrExpression) {
                return new AndExpression(tenantException, new Parenthesis(where));
            } else {
                return new AndExpression(tenantException, where);
            }
        }
        return tenantException;
    }


}
