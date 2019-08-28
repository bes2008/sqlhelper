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

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jinuo.fang
 */
public class OrderBy implements Serializable, Iterable<OrderByItem> {
    public static final OrderBy EMPTY = new OrderBy();
    private final Map<String, OrderByItem> items = new LinkedHashMap<String, OrderByItem>();

    public boolean isValid() {
        return !items.isEmpty();
    }

    public void add(OrderByItem item) {
        items.put(item.getExpression(), item);
    }

    public void addAsc(String expression) {
        add(new OrderByItem(expression, true));
    }

    public void addDesc(String expression) {
        add(new OrderByItem(expression, false));
    }

    @NonNull
    @Override
    public Iterator<OrderByItem> iterator() {
        return items.values().iterator();
    }

    @Override
    public String toString() {
        return Strings.join(",", Pipeline.<OrderByItem>of(items.values()).map(new Function<OrderByItem, String>() {
            @Override
            public String apply(OrderByItem item) {
                return item.toString();
            }
        }).getAll());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        OrderBy that = (OrderBy) object;
        return this.toString().equalsIgnoreCase(that.toString());
    }

    @Override
    public int hashCode() {
        return Pipeline.<OrderByItem>of(items.values()).map(new Function<OrderByItem, Integer>() {
            @Override
            public Integer apply(OrderByItem item) {
                return item.hashCode();
            }
        }).sum().intValue();
    }

    public void setComparator(String itemExpression, Comparator comparator) {
        OrderByItem item = items.get(itemExpression);
        if (item != null) {
            item.setComparator(comparator);
        }
    }
}
