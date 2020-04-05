package com.jn.sqlhelper.tests;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.dialect.expression.*;
import org.junit.Assert;
import org.junit.Test;

import static com.jn.sqlhelper.dialect.expression.SQLExpressions.*;

public class ExpressionTests {

    private SymbolExpression name = new SymbolExpression("name_");
    private StringExpression value = new StringExpression("3");

    @Test
    public void valueExpressionTests() {
        System.out.println("==========value expression tests start==========");
        testExpression(new IntegerOrLongExpression(3), new IntegerOrLongExpressionBuilder().value(3L).build());
        testExpression(new DoubleExpression(2.0), new DoubleExpressionBuilder().value(2.00D).build());
        testExpression(new StringExpression("str"), new StringExpressionBuilder().value("str").build());
        System.out.println("==========value expression tests finish==========");
    }

    private void testExpression(SQLExpression expression1, SQLExpression expression2) {
        System.out.println(expression1);
        System.out.println(expression2);
        Assert.assertEquals(expression1, expression2);
    }

    @Test
    public void testEqualExpression() {
        System.out.println("====expression [=] test start====");
        // =
        EqualExpression equalExpression = new EqualExpression();
        equalExpression.setLeft(name);
        equalExpression.setRight(value);
        testExpression(equalExpression, new EqualBuilder()
                .left("name_", true)
                .right("3")
                .build()
        );
        System.out.println("====expression [=] test finish====");
    }

    @Test
    public void testNotEqualExpression() {
        System.out.println("====expression [!=] test start====");
        // !=
        NotEqualExpression notEqualExpression = new NotEqualExpression();
        notEqualExpression.setLeft(name);
        notEqualExpression.setRight(value);
        testExpression(notEqualExpression, new NotEqualBuilder()
                .left(name)
                .right(value)
                .build()
        );
        System.out.println("====expression [!=] test finish====");
    }

    @Test
    public void testInExpression() {
        System.out.println("====expression [in] test start====");
        // in
        InExpression inExpression = new InExpression();
        inExpression.setLeft(name);
        ListExpression listExpression = new ListExpressionBuilder()
                .addValue("zhangsan")
                .addValue("lisi")
                .addValues(Collects.newArrayList("wangwu", "zhaoliu"))
                .build();
        inExpression.setRight(listExpression);

        testExpression(inExpression, new InBuilder()
                .left(name)
                .addValue("zhangsan")
                .addValue("lisi")
                .addValues(Collects.newArrayList("wangwu", "zhaoliu"))
                .build()
        );
        System.out.println("====expression [in] test finish====");


        System.out.println("====expression [not in] test start====");
        // not in
        inExpression  = new InExpression(true);
        inExpression.setLeft(name);
        inExpression.setRight(listExpression);
        testExpression(inExpression, new InBuilder(true)
                .left(name)
                .addValue("zhangsan")
                .addValue("lisi")
                .addValues(Collects.newArrayList("wangwu", "zhaoliu"))
                .build()
        );
        System.out.println("====expression [not in] test finish====");
    }


}
