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

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Function;
import com.jn.sqlhelper.dialect.SqlSymbolMapper;
import com.jn.sqlhelper.dialect.symbolmapper.NoopSymbolMapper;

import java.util.Comparator;
import java.util.List;

/**
 * Usage:
 * <pre>
 *  OrderBy orderBy = new ProgramStyleOrderByBuilder()
 *      .column("timeline")
 *      .asc("userXXX")
 *      .desc("userYYY")
 *      .column("userZZZ", DESC)
 *      .sqlSymbolMapper(new CamelToUnderlineSymbolMapper())
 *      .build();
 *  </pre>
 */
public class ProgramStyleOrderByBuilder implements OrderByBuilder<Object> {
    private List<OrderByItem> orderByItems = Collects.emptyArrayList();
    private OrderByItem currentItem;

    private SqlSymbolMapper sqlSymbolMapper = NoopSymbolMapper.DEFAULT;

    public ProgramStyleOrderByBuilder() {
    }

    public ProgramStyleOrderByBuilder sqlSymbolMapper(SqlSymbolMapper symbolMapper) {
        if (symbolMapper != null) {
            this.sqlSymbolMapper = symbolMapper;
        }
        return this;
    }


    public ProgramStyleOrderByBuilder column(String column) {
        Preconditions.checkNotNull(column);
        if (currentItem == null) {
            currentItem = new OrderByItem(column);
            currentItem.setType(OrderByType.ASC);
            orderByItems.add(currentItem);
            return this;
        }
        if (!column.equalsIgnoreCase(currentItem.getExpression())) {
            currentItem = null;
            column(column);
        }
        return this;
    }

    private ProgramStyleOrderByBuilder order(@Nullable OrderByType orderByType, @Nullable Comparator comparator) {
        orderByType = orderByType == null ? OrderByType.ASC : orderByType;
        Preconditions.checkNotNull(currentItem, "you should set which column to order first");
        currentItem.setType(orderByType);
        currentItem.setComparator(comparator);
        return this;
    }

    public ProgramStyleOrderByBuilder column(String column, OrderByType orderByType) {
        return column(column, orderByType, null);
    }

    public ProgramStyleOrderByBuilder column(String column, OrderByType orderByType, Comparator comparator) {
        column(column);
        return order(orderByType, comparator);
    }

    public ProgramStyleOrderByBuilder asc(String column) {
        return column(column, OrderByType.ASC);
    }

    public ProgramStyleOrderByBuilder desc(String column) {
        return column(column, OrderByType.DESC);
    }

    public OrderBy build(){
        return build(null);
    }

    @Override
    public OrderBy build(Object object) {
        OrderBy orderBy = new OrderBy();

        orderBy.addAll(Collects.map(orderByItems, new Function<OrderByItem, OrderByItem>() {
            @Override
            public OrderByItem apply(OrderByItem item) {
                item.setExpression(sqlSymbolMapper.apply(item.getExpression()));
                return item;
            }
        }));

        return orderBy;
    }
}
