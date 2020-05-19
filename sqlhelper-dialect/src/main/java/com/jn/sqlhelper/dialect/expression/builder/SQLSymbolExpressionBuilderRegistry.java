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

import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.sqlhelper.dialect.expression.ExpressionSymbol;

import java.util.HashMap;
import java.util.Map;

import static com.jn.sqlhelper.dialect.expression.ExpressionSymbol.*;
import static com.jn.sqlhelper.dialect.expression.builder.SQLExpressionBuilders.*;

public class SQLSymbolExpressionBuilderRegistry implements Initializable {

    private Map<ExpressionSymbol, Class<? extends SQLExpressionBuilder>> registry = new HashMap<ExpressionSymbol, Class<? extends SQLExpressionBuilder>>();

    public SQLExpressionBuilder find(ExpressionSymbol symbol) {
        return null;
    }

    private void register(ExpressionSymbol symbol, Class<? extends SQLExpressionBuilder> builderClass) {
        registry.put(symbol, builderClass);
    }


    @Override
    public void init() throws InitializationException {
        registerBuiltin();
    }

    private void registerBuiltin() {
        register(ALL, AllBuilder.class);
        register(ANY, AnyBuilder.class);

        register(AND, AndBuilder.class);
        register(OR, OrBuilder.class);

        register(BETWEEN_AND, BetweenAndBuilder.class);

        register(ADD, AddBuilder.class);
        register(SUBTRACT, SubtractBuilder.class);
        register(MULTIPLE, MultipleBuilder.class);
        register(DIVIDE, DivideBuilder.class);
        register(MODE, ModeBuilder.class);

        register(EQ, EqualBuilder.class);
        register(GE, GreaterOrEqualBuilder.class);
        register(GT, GreaterThanBuilder.class);
        register(LE, LesserOrEqualBuilder.class);
        register(LT, LesserThanBuilder.class);
        register(NE, NotEqualBuilder.class);

        register(IN, InBuilder.class);
        register(IS_NULL, IsNullBuilder.class);
        register(EXISTS, ExistsBuilder.class);
        register(LIKE, LikeBuilder.class);
    }
}
