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

import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.dialect.expression.SQLExpression;
import com.jn.sqlhelper.dialect.expression.builder.SQLSymbolExpressionBuilderRegistry;

public interface ColumnEvaluationExpressionSupplier extends Supplier<ColumnEvaluation, SQLExpression> {
    void setExpressionBuilderRegistry(SQLSymbolExpressionBuilderRegistry registry);
}
