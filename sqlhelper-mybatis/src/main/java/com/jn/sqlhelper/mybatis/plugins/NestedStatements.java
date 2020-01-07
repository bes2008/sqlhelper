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

package com.jn.sqlhelper.mybatis.plugins;

import com.jn.langx.util.Objects;
import com.jn.sqlhelper.dialect.SqlRequestContext;
import com.jn.sqlhelper.dialect.SqlRequestContextHolder;
import org.apache.ibatis.mapping.MappedStatement;

public class NestedStatements {
    public static boolean isNestedStatement(MappedStatement mappedStatement) {
        SqlRequestContext context = SqlRequestContextHolder.getInstance().get();
        if (Objects.isNull(context)) {
            return false;
        }
        String querySqlId = context.getString(MybatisSqlRequestContextKeys.QUERY_SQL_ID);
        if (querySqlId != null && querySqlId.equals(mappedStatement.getId())) {
            return false;
        }
        return true;
    }
}
