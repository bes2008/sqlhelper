package com.jn.sqlhelper.tests;

import com.jn.langx.util.collection.Collects;
import com.jn.sqlhelper.dialect.expression.*;
import org.junit.Assert;
import org.junit.Test;

import static com.jn.sqlhelper.dialect.expression.SQLExpressions.*;

public class ExpressionTests {

    private ColumnExpression name = new ColumnExpression("user", "name_");
    private StringExpression stringValue = new StringExpression("hello");
    private IntegerOrLongExpression intValue = new IntegerOrLongExpression(3);

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
        equalExpression.setRight(stringValue);
        testExpression(equalExpression, new EqualBuilder()
                .left(new ColumnExpression("user", "name_"))
                .right("hello")
                .build()
        );
        System.out.println("====expression [=] test finish====");

        System.out.println("====expression [!=] test start====");
        // !=
        NotEqualExpression notEqualExpression = new NotEqualExpression();
        notEqualExpression.setLeft(name);
        notEqualExpression.setRight(stringValue);
        testExpression(notEqualExpression, new NotEqualBuilder()
                .left(name)
                .right(stringValue)
                .build()
        );
        System.out.println("====expression [!=] test finish====");
    }

    @Test
    public void testGreaterThanExpression() {
        System.out.println("====expression [>] test start====");
        // !=
        GreaterThanExpression gtExpression = new GreaterThanExpression();
        gtExpression.setLeft(name);
        gtExpression.setRight(intValue);
        testExpression(gtExpression, new GreaterThanBuilder()
                .left(name)
                .right(intValue)
                .build()
        );
        System.out.println("====expression [>] test finish====");

        System.out.println("====expression [>=] test start====");
        // !=
        GreaterOrEqualExpression geExpression = new GreaterOrEqualExpression();
        geExpression.setLeft(name);
        geExpression.setRight(intValue);
        testExpression(geExpression, new GreaterOrEqualBuilder()
                .left(name)
                .right(intValue)
                .build()
        );
        System.out.println("====expression [>=] test finish====");
    }

    @Test
    public void testLesserThanExpression() {
        System.out.println("====expression [<] test start====");
        // !=
        LesserThanExpression ltExpression = new LesserThanExpression();
        ltExpression.setLeft(name);
        ltExpression.setRight(intValue);
        testExpression(ltExpression, new LesserThanBuilder()
                .left(name)
                .right(intValue)
                .build()
        );
        System.out.println("====expression [<] test finish====");

        System.out.println("====expression [<=] test start====");
        // !=
        LesserOrEqualExpression leExpression = new LesserOrEqualExpression();
        leExpression.setLeft(name);
        leExpression.setRight(intValue);
        testExpression(leExpression, new LesserOrEqualBuilder()
                .left(name)
                .right(intValue)
                .build()
        );
        System.out.println("====expression [<=] test finish====");
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
        inExpression = new InExpression(true);
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

    @Test
    public void testBetweenExpression() {
        System.out.println("====expression [between and] test start====");
        BetweenAndExpression ba = new BetweenAndExpression();
        ba.setTarget(name);
        ba.setLow(new IntegerOrLongExpression(100));
        ba.setHigh(new IntegerOrLongExpression(2000));

        BetweenAndBuilder builder = new BetweenAndBuilder()
                .low(100)
                .high(2000)
                .target(name);
        testExpression(ba, builder.build());
        System.out.println("====expression [between and] test finish====");


        System.out.println("====expression [not between and] test start====");
        ba.not(true);
        testExpression(ba, builder.not(true).build());
        System.out.println("====expression [not between and] test finish====");

    }

    @Test
    public void testExistsExpression() {
        System.out.println("====expression [exists] test start====");
        StatementExpression statement = new StatementExpression("select * from table");
        ExistsExpression ba = new ExistsExpression();
        ba.setTarget(statement);

        testExpression(ba, new ExistsBuilder().target(statement).build());
        System.out.println("====expression [exists] test finish====");


        System.out.println("====expression [not exists] test start====");
        ba.not(true);
        testExpression(ba, new ExistsBuilder(true).target(statement).build());
        System.out.println("====expression [not exists] test finish====");
    }

    @Test
    public void testLikeExpression() {
        System.out.println("====expression [like] test start====");

        LikeExpression likeExpression = new LikeExpression();
        likeExpression.setLeft(name);
        likeExpression.setRight(new StringExpression("%zhangsan%"));

        testExpression(likeExpression, new LikeBuilder()
                .left(name)
                .right("%zhangsan%")
                .build());
        System.out.println("====expression [like] test finish====");
    }

    @Test
    public void testAndExpression() {
        System.out.println("====expression [and] test start====");

        AndExpression and = new AndExpression();
        LikeExpression likeExpression = new LikeExpression();
        likeExpression.setLeft(name);
        likeExpression.setRight(new StringExpression("%zhangsan%"));


        GreaterOrEqualExpression ge = new GreaterOrEqualExpression();
        ColumnExpression statement = new ColumnExpression("user", "age");
        ge.setLeft(statement);
        ge.setRight(new IntegerOrLongExpression(3));

        and.setLeft(likeExpression);
        and.setRight(ge);

        testExpression(and, new AndBuilder()
                .left(new LikeBuilder()
                        .left(name)
                        .right("%zhangsan%")
                        .build())
                .right(new GreaterOrEqualBuilder()
                        .left(statement)
                        .right(new IntegerOrLongExpressionBuilder()
                                .value(3)
                                .build())
                        .build()).build());
        System.out.println("====expression [and] test finish====");
    }

    @Test
    public void testOrExpression() {
        System.out.println("====expression [or] test start====");

        LikeExpression likeExpression = new LikeExpression();
        likeExpression.setLeft(name);
        likeExpression.setRight(new StringExpression("%zhangsan%"));

        GreaterOrEqualExpression ge = new GreaterOrEqualExpression();
        ColumnExpression statement = new ColumnExpression("user", "age");
        ge.setLeft(statement);
        ge.setRight(new IntegerOrLongExpression(3));

        OrExpression or = new OrExpression();
        or.setLeft(likeExpression);
        or.setRight(ge);

        testExpression(or, new OrBuilder()
                .left(new LikeBuilder()
                        .left(name)
                        .right("%zhangsan%")
                        .build())
                .right(new GreaterOrEqualBuilder()
                        .left(statement)
                        .right(new IntegerOrLongExpressionBuilder()
                                .value(3)
                                .build())
                        .build()).build());
        System.out.println("====expression [or] test finish====");
    }

    @Test
    public void testAllExpression() {
        System.out.println("====expression [all] test start====");
        StatementExpression statement = new StatementExpression("select column from table");
        AllExpression all = new AllExpression();
        all.setTarget(statement);

        GreaterOrEqualExpression ge = new GreaterOrEqualExpression();
        ColumnExpression age = new ColumnExpression("user", "age");
        ge.setLeft(age);
        ge.setRight(all);

        testExpression(ge, new GreaterOrEqualBuilder()
                .left(age)
                .right(all)
                .build());
        System.out.println("====expression [all] test finish====");
    }

    @Test
    public void testAnyExpression() {
        System.out.println("====expression [any] test start====");
        StatementExpression statement = new StatementExpression("select column from table");
        AllExpression any = new AllExpression();
        any.setTarget(statement);

        GreaterOrEqualExpression ge = new GreaterOrEqualExpression();
        ColumnExpression age = new ColumnExpression("user", "age");
        ge.setLeft(age);
        ge.setRight(any);

        testExpression(ge, new GreaterOrEqualBuilder()
                .left(age)
                .right(any)
                .build());

        System.out.println("====expression [any] test finish====");
    }

    public void testIsNullExpression() {
        IsNullExpression isNull = new IsNullExpression();
        isNull.setTarget(name);

        testExpression(isNull, new IsNullBuilder().target(name).build());
    }


}
