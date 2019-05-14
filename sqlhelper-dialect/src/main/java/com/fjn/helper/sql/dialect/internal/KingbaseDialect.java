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

package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;

public class KingbaseDialect extends AbstractDialect {
    public KingbaseDialect(){
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, RowSelection rowSelection) {
                boolean hasOffset = rowSelection.getOffset() > 0;
                return getLimitString(sql, hasOffset);
            }

            @Override
            public String getLimitString(String sql, boolean hasOffset) {
                sql = sql.trim();
                while (sql.endsWith(";")){
                    sql = sql.substring(0, sql.length() -1);
                }
                String forUpdateClause = " for update";
                boolean isForUpdate = false;
                if(sql.toLowerCase().endsWith(forUpdateClause)){
                    sql = sql.substring(0, sql.length() -forUpdateClause.length());
                    isForUpdate = true;
                }
                StringBuilder pagingSql = new StringBuilder(sql.length() + 100);

                if(hasOffset){
                    pagingSql.append(sql).append(" limit ? offset ? ");
                }else{
                    pagingSql.append(sql).append(" limit ? ");
                }
                if(isForUpdate){
                    pagingSql.append(forUpdateClause);
                }
                return pagingSql.toString();
            }
        });
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }
}
