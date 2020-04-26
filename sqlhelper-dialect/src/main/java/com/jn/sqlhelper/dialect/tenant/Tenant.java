package com.jn.sqlhelper.dialect.tenant;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.sqlhelper.dialect.orderby.OrderByType;
import com.jn.sqlhelper.dialect.orderby.SqlStyleOrderByBuilder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huxiongming
 */
public class Tenant {


    private String tenantColumn;

    private List<String> multipleTenantValues;
    private boolean isMultipleTenant;

    private String singleTenantValues;

    public void setTenantColumn(String tenantColumn) {
        this.tenantColumn = tenantColumn;
    }

    public List<String> getMultipleTenantValues() {
        return multipleTenantValues;
    }

    public void setMultipleTenantValues(List<String> multipleTenantValues) {
        this.multipleTenantValues = multipleTenantValues;
    }

    public boolean isMultipleTenant() {
        return isMultipleTenant;
    }

    public void setMultipleTenant(boolean multipleTenant) {
        isMultipleTenant = multipleTenant;
    }

    public String getSingleTenantValues() {
        return singleTenantValues;
    }

    public void setSingleTenantValues(String singleTenantValues) {
        this.singleTenantValues = singleTenantValues;
    }


    public Expression getTenant(boolean where) {
        if (where && isMultipleTenant) {
            return multipleTenantCondition();
        } else {
            return singleTenantCondition();
        }
    }

    public String getTenantColumn() {
        return this.tenantColumn;
    }

    private Expression singleTenantCondition() {
        return new StringValue(singleTenantValues);
    }

    public boolean doTableFilter(String tableName) {
        return false;
    }


    private Expression multipleTenantCondition() {
        final InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(new Column(this.tenantColumn));
        final ExpressionList itemsList = new ExpressionList();
        final List<Expression> inValues = new ArrayList<>(this.multipleTenantValues.size());
        Collects.forEach(this.multipleTenantValues, new Predicate<String>() {
            @Override
            public boolean test(String test) {
                return true;
            }
        }, new Consumer<String>() {
            @Override
            public void accept(String item) {
                inValues.add(new StringValue(item));
            }
        });
        itemsList.setExpressions(inValues);
        inExpression.setRightItemsList(itemsList);
        return inExpression;
    }
}
