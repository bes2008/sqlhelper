package com.jn.sqlhelper.tests;

import com.jn.sqlhelper.dialect.ast.expression.*;
import org.junit.Assert;
import org.junit.Test;

import static com.jn.sqlhelper.dialect.ast.expression.SQLExpressions.*;

public class ExpressionTests {
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
    public void compareExpressionTests() {
        System.out.println("==========compare expression tests start==========");

        SymbolExpression name = new SymbolExpression("name_");
        StringExpression value = new StringExpression("3");
        // =
        EqualExpression equalExpression = new EqualExpression();
        equalExpression.setLeft(name);
        equalExpression.setRight(value);
        testExpression(equalExpression, new EqualBuilder()
                .left("name_", true)
                .right("3")
                .build()
        );

        // <>
        NotEqualExpression notEqualExpression = new NotEqualExpression();
        notEqualExpression.setLeft(name);
        notEqualExpression.setRight(value);
        testExpression(notEqualExpression, new NotEqualBuilder()
                .left(name)
                .right(value)
                .build()
        );
        System.out.println("==========compare expression tests start==========");
    }

    public void AndOrNotExpressionTests() {
    }

}
