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

package com.jn.sqlhelper.mybatisplus.tableinfo;

import java.util.List;

public interface TableInfoWrapper {
    String getSqlStatement(String sqlMethod);
    boolean havePK();
    String getKeySqlSelect();
    String getAllSqlSelect();
    String getKeyInsertSqlProperty(final String prefix, final boolean newLine);
    String getKeyInsertSqlColumn(final boolean newLine);
    String getAllInsertSqlPropertyMaybeIf(final String prefix);
    String getAllInsertSqlColumnMaybeIf(final String prefix);
    String getAllSqlWhere(boolean ignoreLogicDelFiled, boolean withId, final String prefix);
    String getAllSqlSet(boolean ignoreLogicDelFiled, final String prefix);
    String getLogicDeleteSql(boolean startWithAnd, boolean isWhere);
    List getFieldList();
    boolean isLogicDelete();
}
