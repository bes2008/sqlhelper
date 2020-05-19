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

import com.jn.sqlhelper.common.ddl.model.internal.JdbcType;
import com.jn.sqlhelper.dialect.expression.ExpressionSymbol;

import java.util.List;

public class ColumnEvaluation {
    /**
     * 列坐标
     */
    private String catalog;
    private String schema;
    private String table;
    private String column;

    private JdbcType jdbcType;

    private ExpressionSymbol symbol;

    private boolean not;

    List values;

    public String getCatalog() {
        return catalog;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public ExpressionSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(ExpressionSymbol symbol) {
        this.symbol = symbol;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }
}
