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

package com.jn.sqlhelper.dialect.expression.columnevaluation;

import com.jn.langx.util.Objects;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.sqlhelper.dialect.expression.*;
import com.jn.sqlhelper.dialect.expression.builder.*;

public class BuiltinColumnEvaluationExpressionSupplier implements ColumnEvaluationExpressionSupplier {
    private SQLSymbolExpressionBuilderRegistry registry;

    @Override
    public SQLExpression get(ColumnEvaluation columnEvaluation) {
        SQLExpressionBuilder expressionBuilder = registry.find(columnEvaluation.getSymbol());
        SQLExpression expression = createExpression(expressionBuilder, columnEvaluation);
        if (expression != null) {
            if (expression instanceof Notable) {
                ((Notable) expression).not(columnEvaluation.isNot());
            }
        }
        return expression;
    }

    private SQLExpression createExpression(SQLExpressionBuilder expressionBuilder, ColumnEvaluation columnEvaluation) {
        ColumnExpression columnExpression = new SQLExpressionBuilders.ColumnBuilder()
                .catalog(columnEvaluation.getCatalog())
                .schema(columnEvaluation.getSchema())
                .table(columnEvaluation.getTable())
                .column(columnEvaluation.getColumn())
                .build();
        if (expressionBuilder instanceof UnaryOperatorExpressionBuilder) {
            UnaryOperatorExpressionBuilder builder = (UnaryOperatorExpressionBuilder) expressionBuilder;
            return builder.target(columnExpression).build();
        } else if (expressionBuilder instanceof BinaryOperatorExpressionBuilder) {
            final BinaryOperatorExpressionBuilder builder = (BinaryOperatorExpressionBuilder) expressionBuilder;
            builder.left(columnExpression);
            Collects.forEach(columnEvaluation.getValues(), new Consumer<Object>() {
                @Override
                public void accept(Object value) {
                    builder.right(new PlaceholderExpression());
                }
            });
            return builder.build();
        } else if (expressionBuilder instanceof BetweenAndExpression) {
            Preconditions.checkArgument(Objects.length(columnEvaluation.getValues()) == 2);
            return new SQLExpressionBuilders.BetweenAndBuilder()
                    .target(columnExpression)
                    .low(new PlaceholderExpression())
                    .high(new PlaceholderExpression())
                    .build();
        }
        return null;
    }

    public SQLSymbolExpressionBuilderRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(SQLSymbolExpressionBuilderRegistry registry) {
        this.registry = registry;
    }
}
