package com.jn.sqlhelper.dialect.instrument.orderby;

import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.iter.IteratorIterable;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.sqlhelper.dialect.instrument.TransformConfig;
import com.jn.sqlhelper.dialect.sqlparser.SqlStatementWrapper;
import com.jn.sqlhelper.dialect.sqlparser.StringSqlStatementWrapper;

import java.util.List;
import java.util.ServiceLoader;

public class DefaultOrderByTransformer implements OrderByTransformer {
    private final List<OrderByTransformer> delegates = Collects.emptyArrayList();
    private final SimpleOrderByTransformer simpleTransformer = new SimpleOrderByTransformer();
    private boolean inited = false;

    @Override
    public final boolean enabled() {
        return true;
    }

    @Override
    public boolean transformable(SqlStatementWrapper statementWrapper) {
        return true;
    }

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            ServiceLoader<OrderByTransformer> loader = ServiceLoader.load(OrderByTransformer.class);
            Collects.forEach(new IteratorIterable<OrderByTransformer>(loader.iterator()), new Consumer<OrderByTransformer>() {
                @Override
                public void accept(OrderByTransformer orderByTransformer) {
                    delegates.add(orderByTransformer);
                }
            });
            inited = true;
        }
    }

    @Override
    public SqlStatementWrapper transform(final SqlStatementWrapper statement, final TransformConfig config) {
        try {
            Collects.forEach(delegates, new Predicate<OrderByTransformer>() {
                @Override
                public boolean test(OrderByTransformer transformer) {
                    return transformer.enabled() && transformer.transformable(statement);
                }
            }, new Consumer<OrderByTransformer>() {
                @Override
                public void accept(OrderByTransformer transformer) {
                    transformer.transform(statement, config);
                }
            });
        } catch (Throwable ex) {
            if (!simpleTransformer.transformable(statement)) {
                SqlStatementWrapper<String> sw = new StringSqlStatementWrapper();
                sw.setOriginalSql(statement.getOriginalSql());
                sw.setChanged(statement.isChanged());
                sw.setStatement(statement.getSql());
                simpleTransformer.transform(sw, config);
                return sw;
            } else {
                simpleTransformer.transform(statement, config);
            }
        }
        return statement;
    }
}
