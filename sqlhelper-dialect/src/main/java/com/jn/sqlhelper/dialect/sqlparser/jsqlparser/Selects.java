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

package com.jn.sqlhelper.dialect.sqlparser.jsqlparser;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.util.List;

public class Selects {
    public static PlainSelect extractPlainSelect(SelectBody selectBody) {
        if (selectBody == null) {
            return null;
        }
        if (selectBody instanceof PlainSelect) {
            return (PlainSelect) selectBody;
        }

        if (selectBody instanceof WithItem) {
            SelectBody subSelectBody = ((WithItem) selectBody).getSelectBody();
            if (subSelectBody != null) {
                return extractPlainSelect(subSelectBody);
            } else {
                return null;
            }
        }

        if (selectBody instanceof ValuesStatement) {
            return null;
        }

        if (selectBody instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) selectBody;
            List<SelectBody> selectBodyList = setOperationList.getSelects();
            if (selectBodyList != null && !selectBodyList.isEmpty()) {
                return extractPlainSelect(selectBodyList.get(selectBodyList.size() - 1));
            }
        }
        return null;

    }

    public static boolean columnEquals(Column column1, Column column2) {
        if (column1 == null && column2 == null) {
            return true;
        }
        if (column1 == null || column2 == null) {
            return false;
        }
        return column1.getFullyQualifiedName().equalsIgnoreCase(column2.getFullyQualifiedName());
    }

    public static boolean expressionEquals(Expression expr1, Expression expr2) {
        if (expr1 == null && expr2 == null) {
            return true;
        }
        if (expr1 == null || expr2 == null) {
            return false;
        }

        if (expr1 instanceof Column && expr2 instanceof Column) {
            return columnEquals((Column) expr1, (Column) expr2);
        }
        return expr1.toString().equalsIgnoreCase(expr2.toString());
    }
}
