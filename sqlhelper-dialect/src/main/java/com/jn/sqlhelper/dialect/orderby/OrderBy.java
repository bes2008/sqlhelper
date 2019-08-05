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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderBy implements Serializable, Iterable<OrderByItem> {

    private final List<OrderByItem> items = new ArrayList<OrderByItem>();

    public boolean isValid() {
        return !items.isEmpty();
    }

    public void add(OrderByItem item) {
        items.add(item);
    }

    public void addAsc(String expression) {
        items.add(new OrderByItem(expression, true));
    }

    public void addDesc(String expression) {
        items.add(new OrderByItem(expression, false));
    }

    @Override
    public Iterator<OrderByItem> iterator() {
        return items.iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(256);
        for (int i = 0; i < items.size(); i++) {
            OrderByItem item = items.get(i);
            if (i == 0) {
                builder.append(item.toString());
            } else {
                builder.append(",").append(item.toString());
            }
        }
        return builder.toString();
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
        int hashCode = 0;
        for (OrderByItem item : items) {
            hashCode += item.hashCode();
        }
        return hashCode;
    }
}
