package com.jn.sqlhelper.mango;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.dialect.parameter.BaseQueryParameters;
import org.jfaster.mango.binding.BoundSql;
import org.jfaster.mango.type.TypeHandler;

import java.util.List;

public class MangoQueryParameters extends BaseQueryParameters<BoundSql> {

    @Override
    public void setParameters(BoundSql parameters) {
        super.setParameters(parameters);
    }

    @Override
    public void setParameters(BoundSql boundSql, int beforeSubqueryCount, int afterSubqueryCount) {
        super.setParameters(boundSql, beforeSubqueryCount, afterSubqueryCount);
        this.subqueryParameters = boundSql.copy();

        List<Object> beforeArgs = Collects.emptyArrayList();
        List<TypeHandler<?>> beforeTypeHandlers = Collects.emptyArrayList();
        for (int i = 0; i < beforeSubqueryCount; i++) {
            beforeArgs.add(subqueryParameters.getArgs().remove(0));
            beforeTypeHandlers.add(subqueryParameters.getTypeHandlers().remove(0));
        }
        this.beforeSubqueryParameters = new BoundSql(boundSql.getSql(), beforeArgs, beforeTypeHandlers);

        int c = 0;
        List<Object> afterArgs = Collects.emptyArrayList();
        List<TypeHandler<?>> afterTypeHandlers = Collects.emptyArrayList();
        this.afterSubqueryParameters = new BoundSql(boundSql.getSql());
        for (int i = subqueryParameters.getArgs().size() - 1; i < afterSubqueryCount; i--) {
            afterArgs.add(subqueryParameters.getArgs().remove(i));
            afterTypeHandlers.add(subqueryParameters.getTypeHandlers().remove(i));
            c++;
        }
        this.afterSubqueryParameters = new BoundSql(boundSql.getSql(), afterArgs, afterTypeHandlers);
    }
}
