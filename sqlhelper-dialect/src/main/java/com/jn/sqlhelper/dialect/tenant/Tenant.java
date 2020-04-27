package com.jn.sqlhelper.dialect.tenant;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.sqlhelper.dialect.expression.*;

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


    public SQLExpression getTenant(boolean where) {
        if (where && isMultipleTenant) {
            return multipleTenantCondition();
        } else {
            return singleTenantCondition();
        }
    }

    public String getTenantColumn() {
        return this.tenantColumn;
    }

    private SQLExpression singleTenantCondition() {
        EqualExpression equalExpression=new EqualExpression();
        equalExpression.setLeft(new SQLExpressions.ColumnBuilder().column(this.tenantColumn).build());
        equalExpression.setRight(new StringExpression(this.singleTenantValues));
        return equalExpression;
    }

    public boolean doTableFilter(String tableName) {
        return false;
    }


    private SQLExpression multipleTenantCondition() {
        InExpression inExpression = new InExpression();
        inExpression.setLeft(new ColumnExpression(this.tenantColumn));
        final ListExpression listExpression = new SQLExpressions.ListExpressionBuilder().addValues(this.multipleTenantValues).build();
        inExpression.setRight(listExpression);
        return inExpression;
    }

    @Override
    public String toString() {
        return "_tenant";
    }
}
