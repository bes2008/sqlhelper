package com.jn.sqlhelper.jsqlparser.expression;

import com.jn.langx.annotation.Singleton;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.spi.CommonServiceProvider;
import com.jn.sqlhelper.dialect.expression.SQLExpression;
import net.sf.jsqlparser.expression.Expression;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ExpressionConverterRegistry {
    private static final ExpressionConverterRegistry instance = new ExpressionConverterRegistry();

    private Map<Class<? extends SQLExpression>, ExpressionConverter> standardExpressionConverterMap = new HashMap<Class<? extends SQLExpression>, ExpressionConverter>();
    private Map<Class<? extends Expression>, ExpressionConverter> jsqlparserExpressionConverterMap = new HashMap<Class<? extends Expression>, ExpressionConverter>();

    private ExpressionConverterRegistry() {
        registryBuiltins();
    }

    public static ExpressionConverterRegistry getInstance() {
        return instance;
    }

    public void registry(ExpressionConverter converter) {
        Class<? extends SQLExpression> standardExpressionClass = converter.getStandardExpressionClass();
        Class<? extends Expression> jSqlParserExpressionClass = converter.getJSqlParserExpressionClass();
        Preconditions.checkNotNull(standardExpressionClass);
        Preconditions.checkNotNull(jSqlParserExpressionClass);
        standardExpressionConverterMap.put(standardExpressionClass, converter);
        jsqlparserExpressionConverterMap.put(jSqlParserExpressionClass, converter);
    }

    private void registryBuiltins(){
        Collects.forEach(CommonServiceProvider.loadService(ExpressionConverter.class), new Consumer<ExpressionConverter>() {
            @Override
            public void accept(ExpressionConverter converter) {
                registry(converter);
            }
        });
    }

    public ExpressionConverter getExpressionConverterByStandardExpressionClass(Class<? extends SQLExpression>  clazz){
        return standardExpressionConverterMap.get(clazz);
    }
    public ExpressionConverter getExpressionConverterByJSqlParserExpressionClass(Class<? extends Expression>  clazz){
        return jsqlparserExpressionConverterMap.get(clazz);
    }
}
