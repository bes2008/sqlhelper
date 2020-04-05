package com.jn.sqlhelper.dialect.expression;

import com.jn.langx.Builder;
import com.jn.langx.expression.operator.BinaryOperator;
import com.jn.langx.expression.operator.UnaryOperator;
import com.jn.langx.util.Numbers;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Supplier0;
import com.jn.langx.util.reflect.type.Primitives;

import java.util.List;

public class SQLExpressions {
    private SQLExpressions() {
    }

    public abstract static class AbstractExpressionBuilder<E extends SQLExpression> implements Builder<E> {
    }

    public static class SymbolBuilder extends AbstractExpressionBuilder<SymbolExpression> {
        private SymbolExpression symbolExpression = new SymbolExpression();

        public SymbolBuilder value(String symbol) {
            symbolExpression.setValue(symbol);
            return this;
        }

        @Override
        public SymbolExpression build() {
            return symbolExpression;
        }
    }

    public static class IntegerOrLongExpressionBuilder extends AbstractExpressionBuilder<IntegerOrLongExpression> {
        private IntegerOrLongExpression expression = new IntegerOrLongExpression();

        public IntegerOrLongExpressionBuilder() {
            super();
        }


        public IntegerOrLongExpressionBuilder value(long value) {
            expression.setValue(value);
            return this;
        }

        public IntegerOrLongExpressionBuilder value(int value) {
            expression.setValue(Numbers.toLong(value));
            return this;
        }

        @Override
        public IntegerOrLongExpression build() {
            return expression;
        }
    }

    public static class DoubleExpressionBuilder extends AbstractExpressionBuilder<DoubleExpression> {
        private DoubleExpression expression = new DoubleExpression();

        public DoubleExpressionBuilder() {
            super();
        }

        public DoubleExpressionBuilder value(double value) {
            expression.setValue(value);
            return this;
        }

        @Override
        public DoubleExpression build() {
            return expression;
        }
    }

    public static class StringExpressionBuilder extends AbstractExpressionBuilder<StringExpression> {
        private StringExpression expression = new StringExpression();

        public StringExpressionBuilder() {
            super();
        }

        public StringExpressionBuilder value(String value) {
            expression.setValue(value);
            return this;
        }

        @Override
        public StringExpression build() {
            return expression;
        }
    }

    public static class ListExpressionBuilder extends AbstractExpressionBuilder<ListExpression> {
        private ListExpression list = new ListExpression();

        public ListExpression getListExpression() {
            return list;
        }

        public ListExpressionBuilder addValue(String value) {
            list.add(new StringExpression(value));
            return this;
        }

        public ListExpressionBuilder addValue(int value) {
            list.add(new IntegerOrLongExpression(value));
            return this;
        }

        public ListExpressionBuilder addValue(long value) {
            list.add(new IntegerOrLongExpression(value));
            return this;
        }

        public ListExpressionBuilder addValue(double value) {
            list.add(new DoubleExpression(value));
            return this;
        }

        public ListExpressionBuilder addValue(SQLExpression expression) {
            list.add(expression);
            return this;
        }

        public ListExpressionBuilder addValues(List<?> values) {
            Collects.forEach(values, new Predicate() {
                @Override
                public boolean test(Object value) {
                    return value != null;
                }
            }, new Consumer() {
                @Override
                public void accept(Object o) {
                    Class clazz = Primitives.wrap(o.getClass());
                    if (clazz == String.class || clazz == Character.class) {
                        addValue((String) o);
                        return;
                    }
                    if (clazz == Short.class || clazz == Byte.class || clazz == Integer.class) {
                        addValue((Integer) o);
                        return;
                    }
                    if (clazz == Long.class) {
                        addValue((Long) o);
                        return;
                    }
                    if (clazz == Float.class || clazz == Double.class) {
                        addValue(Numbers.toDouble((Number) o));
                    }
                    addValue(new ValueExpression(o));
                }
            });
            return this;
        }

        @Override
        public ListExpression build() {
            return list;
        }
    }

    public abstract static class UnaryOperatorExpressionBuilder<E extends SQLExpression & UnaryOperator, T extends UnaryOperatorExpressionBuilder<E, T>> extends AbstractExpressionBuilder<E> {
        protected String operateSymbol; // optional
        protected SQLExpression target; // required
        private Supplier0<E> supplier; // required

        public T supplier(Supplier0<E> supplier) {
            this.supplier = supplier;
            return (T) this;
        }

        public T operateSymbol(String symbol) {
            this.operateSymbol = symbol;
            return (T) this;
        }

        public T target(SQLExpression expression) {
            this.target = expression;
            return (T) this;
        }

        public T target(String expression) {
            return target(expression, false);
        }

        public T target(String expression, boolean isSymbol) {
            return target(isSymbol ? new SymbolExpression(expression) : new StringExpression(expression));
        }

        @Override
        public E build() {
            Preconditions.checkNotNull(target, "the target expression is null");
            Preconditions.checkNotNull(supplier, "the supplier is null");

            E unaryOperator = supplier.get();
            if (Strings.isNotEmpty(operateSymbol)) {
                unaryOperator.setOperateSymbol(operateSymbol);
            }
            unaryOperator.setTarget(target);
            return unaryOperator;
        }
    }

    public abstract static class BinaryOperatorExpressionBuilder<E extends SQLExpression & BinaryOperator, T extends BinaryOperatorExpressionBuilder<E, T>> extends AbstractExpressionBuilder<E> {
        protected String operateSymbol; // optional
        protected SQLExpression left; // required
        protected SQLExpression right; // required
        private Supplier0<E> supplier; // required

        public T supplier(Supplier0<E> supplier) {
            this.supplier = supplier;
            return (T) this;
        }

        public T operateSymbol(String symbol) {
            this.operateSymbol = symbol;
            return (T) this;
        }

        public T left(String expression) {
            return left(expression, true);
        }

        public T left(String expression, boolean isSymbol) {
            left = isSymbol ? new SymbolExpression(expression) : new StringExpression(expression);
            return (T) this;
        }

        public T left(long expression) {
            left = new IntegerOrLongExpression(expression);
            return (T) this;
        }

        public T left(double expression) {
            left = new DoubleExpression(expression);
            return (T) this;
        }

        public T left(SQLExpression expression) {
            left = expression;
            return (T) this;
        }

        public T right(String expression) {
            return right(expression, false);
        }

        public T right(String expression, boolean isSymbol) {
            right = isSymbol ? new SymbolExpression(expression) : new StringExpression(expression);
            return (T) this;
        }

        public T right(long expression) {
            right = new IntegerOrLongExpression(expression);
            return (T) this;
        }

        public T right(double expression) {
            right = new DoubleExpression(expression);
            return (T) this;
        }

        public T right(SQLExpression expression) {
            right = expression;
            return (T) this;
        }

        @Override
        public E build() {
            Preconditions.checkNotNull(left, "left expression is null");
            Preconditions.checkNotNull(right, "right expression is null");
            Preconditions.checkNotNull(supplier, "the supplier is null");

            E binaryOperator = supplier.get();
            if (Strings.isNotEmpty(operateSymbol)) {
                binaryOperator.setOperateSymbol(operateSymbol);
            }
            binaryOperator.setLeft(left);
            binaryOperator.setRight(right);
            return binaryOperator;
        }
    }

    public static class AndBuilder extends BinaryOperatorExpressionBuilder<AndExpression, AndBuilder> {
        public AndBuilder() {
            supplier(new Supplier0<AndExpression>() {
                @Override
                public AndExpression get() {
                    return new AndExpression();
                }
            });
        }
    }

    public static class OrBuilder extends BinaryOperatorExpressionBuilder<OrExpression, OrBuilder> {
        public OrBuilder() {
            supplier(new Supplier0<OrExpression>() {
                @Override
                public OrExpression get() {
                    return new OrExpression();
                }
            });
        }
    }

    public static class EqualBuilder extends BinaryOperatorExpressionBuilder<EqualExpression, EqualBuilder> {
        public EqualBuilder() {
            supplier(new Supplier0<EqualExpression>() {
                @Override
                public EqualExpression get() {
                    return new EqualExpression();
                }
            });
        }
    }

    public static class NotEqualBuilder extends BinaryOperatorExpressionBuilder<NotEqualExpression, NotEqualBuilder> {
        public NotEqualBuilder() {
            supplier(new Supplier0<NotEqualExpression>() {
                @Override
                public NotEqualExpression get() {
                    return new NotEqualExpression();
                }
            });
        }
    }

    public static class GreaterThanBuilder extends BinaryOperatorExpressionBuilder<GreaterThanExpression, GreaterThanBuilder> {
        public GreaterThanBuilder() {
            supplier(new Supplier0<GreaterThanExpression>() {
                @Override
                public GreaterThanExpression get() {
                    return new GreaterThanExpression();
                }
            });
        }
    }

    public static class GreaterOrEqualBuilder extends BinaryOperatorExpressionBuilder<GreaterOrEqualExpression, GreaterOrEqualBuilder> {
        public GreaterOrEqualBuilder() {
            supplier(new Supplier0<GreaterOrEqualExpression>() {
                @Override
                public GreaterOrEqualExpression get() {
                    return new GreaterOrEqualExpression();
                }
            });
        }
    }

    public static class LesserThanBuilder extends BinaryOperatorExpressionBuilder<LesserThanExpression, LesserThanBuilder> {
        public LesserThanBuilder() {
            supplier(new Supplier0<LesserThanExpression>() {
                @Override
                public LesserThanExpression get() {
                    return new LesserThanExpression();
                }
            });
        }
    }

    public static class LesserOrEqualBuilder extends BinaryOperatorExpressionBuilder<LesserOrEqualExpression, LesserOrEqualBuilder> {
        public LesserOrEqualBuilder() {
            supplier(new Supplier0<LesserOrEqualExpression>() {
                @Override
                public LesserOrEqualExpression get() {
                    return new LesserOrEqualExpression();
                }
            });
        }
    }

    public static class InBuilder extends BinaryOperatorExpressionBuilder<InExpression, InBuilder> {
        ListExpressionBuilder listExpressionBuilder = new ListExpressionBuilder();

        public InBuilder() {
            this(false);
        }

        public InBuilder(final boolean not) {
            supplier(new Supplier0<InExpression>() {
                @Override
                public InExpression get() {
                    return new InExpression(not);
                }
            });
            right(listExpressionBuilder.getListExpression());
        }

        public InBuilder addValue(long value) {
            listExpressionBuilder.addValue(value);
            return this;
        }

        public InBuilder addValue(double value) {
            listExpressionBuilder.addValue(value);
            return this;
        }

        public InBuilder addValue(String value) {
            listExpressionBuilder.addValue(value);
            return this;
        }

        public InBuilder addValue(SQLExpression value) {
            listExpressionBuilder.addValue(value);
            return this;
        }

        public InBuilder addValues(List<?> values) {
            listExpressionBuilder.addValues(values);
            return this;
        }

    }

    public static class IsNullBuilder extends UnaryOperatorExpressionBuilder<IsNullExpression, IsNullBuilder> {
        public IsNullBuilder() {
            this(false);
        }

        public IsNullBuilder(final boolean not) {
            supplier(new Supplier0<IsNullExpression>() {
                @Override
                public IsNullExpression get() {
                    return new IsNullExpression(not);
                }
            });
        }
    }

    public static class BetweenAndBuilder extends AbstractExpressionBuilder<BetweenAndExpression> {
        private BetweenAndExpression between = new BetweenAndExpression();

        public BetweenAndBuilder() {
            super();
        }

        public BetweenAndBuilder not(boolean isNotExpression) {
            between.not(isNotExpression);
            return this;
        }

        public BetweenAndBuilder target(SQLExpression target) {
            between.setTarget(target);
            return this;
        }

        public BetweenAndBuilder low(int low) {
            between.setLow(new IntegerOrLongExpression(low));
            return this;
        }


        public BetweenAndBuilder low(long low) {
            between.setLow(new IntegerOrLongExpression(low));
            return this;
        }


        public BetweenAndBuilder low(double low) {
            between.setLow(new DoubleExpression(low));
            return this;
        }

        public BetweenAndBuilder low(String low) {
            between.setLow(new StringExpression(low));
            return this;
        }

        public BetweenAndBuilder low(SQLExpression low) {
            between.setLow(low);
            return this;
        }

        public BetweenAndBuilder high(int low) {
            between.setHigh(new IntegerOrLongExpression(low));
            return this;
        }


        public BetweenAndBuilder high(long low) {
            between.setHigh(new IntegerOrLongExpression(low));
            return this;
        }


        public BetweenAndBuilder high(double low) {
            between.setHigh(new DoubleExpression(low));
            return this;
        }

        public BetweenAndBuilder high(String low) {
            between.setHigh(new StringExpression(low));
            return this;
        }

        public BetweenAndBuilder high(SQLExpression high) {
            between.setHigh(high);
            return this;
        }

        @Override
        public BetweenAndExpression build() {
            return between;
        }
    }

    public static class LikeBuilder extends BinaryOperatorExpressionBuilder<LikeExpression, LikeBuilder> {
        public LikeBuilder() {
            this(false, null);
        }

        public LikeBuilder(final String escape) {
            this(false, escape);
        }

        public LikeBuilder(final boolean isNotExpression, final String escape) {
            supplier(new Supplier0<LikeExpression>() {
                @Override
                public LikeExpression get() {
                    LikeExpression like = new LikeExpression(isNotExpression);
                    like.setEscape(escape);
                    return like;
                }
            });
        }
    }

    public static class AllBuilder extends UnaryOperatorExpressionBuilder<AllExpression, AllBuilder> {
        public AllBuilder() {
            supplier(new Supplier0<AllExpression>() {
                @Override
                public AllExpression get() {
                    return new AllExpression();
                }
            });
        }
    }

    public static class AnyBuilder extends UnaryOperatorExpressionBuilder<AnyExpression, AnyBuilder> {
        public AnyBuilder() {
            supplier(new Supplier0<AnyExpression>() {
                @Override
                public AnyExpression get() {
                    return new AnyExpression();
                }
            });
        }
    }

    public static class ExistsBuilder extends UnaryOperatorExpressionBuilder<ExistsExpression, ExistsBuilder> {
        public ExistsBuilder() {
            this(false);
        }

        public ExistsBuilder(final boolean isNotExpression) {
            supplier(new Supplier0<ExistsExpression>() {
                @Override
                public ExistsExpression get() {
                    return new ExistsExpression(isNotExpression);
                }
            });
        }
    }

    public static boolean isPlaceholderExpression(SQLExpression expression){
        if(expression==null){
            return false;
        }
        if(expression instanceof PlaceholderExpression){
            return true;
        }
        return false;
    }
}
