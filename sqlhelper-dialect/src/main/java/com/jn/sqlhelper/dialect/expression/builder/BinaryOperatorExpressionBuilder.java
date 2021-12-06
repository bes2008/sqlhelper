/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.dialect.expression.builder;

import com.jn.langx.el.expression.operator.BinaryOperator;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.function.Supplier0;
import com.jn.sqlhelper.dialect.expression.*;

public class BinaryOperatorExpressionBuilder<E extends SQLExpression & BinaryOperator, T extends BinaryOperatorExpressionBuilder<E, T>> implements SQLExpressionBuilder<E> {
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

    public T left(String expression, boolean isColumn) {
        left = isColumn ? new ColumnExpression(expression) : new StringExpression(expression);
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

    public T right(String expression, boolean isColumn) {
        right = isColumn ? new ColumnExpression(expression) : new StringExpression(expression);
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