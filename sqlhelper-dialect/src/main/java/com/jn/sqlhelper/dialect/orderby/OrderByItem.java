/*
 * Copyright 2019 the original author or authors.
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

package com.jn.sqlhelper.dialect.orderby;

import java.io.Serializable;

public class OrderByItem implements Serializable {
    private String expression;
    private OrderByType type;

    public OrderByItem() {
    }

    public OrderByItem(String expression, OrderByType type) {
        this.expression = expression;
        this.type = type;
    }

    public OrderByItem(String expression, boolean asc) {
        this(expression, asc ? OrderByType.ASC : OrderByType.DESC);
    }


    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public OrderByType getType() {
        return type;
    }

    public void setType(OrderByType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return " " + expression + " " + type.name();
    }
}
