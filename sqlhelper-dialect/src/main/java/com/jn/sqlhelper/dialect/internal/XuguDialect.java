/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.OracleXLimitHandler;
import com.jn.sqlhelper.dialect.urlparser.XuguUrlParser;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;

import java.sql.CallableStatement;
import java.sql.SQLException;


/**
 * • 暂不支持 DELETE/UPDATE 使用子查询
 * • 暂不支持子查询使用 limit 限制返回记录数，可使用 rownum 进行限制
 * • 暂不支持 DELETE/UPDATE 的多表操作
 * • 暂不支持 MERGE INTO/REPLACE INTO 语法
 */

public class XuguDialect extends AbstractDialect {

    public XuguDialect() {
        super();
        setUrlParser(new XuguUrlParser());
        setLimitHandler(new OracleXLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.INSTANCE);
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
        return col;
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }

    @Override
    public boolean isSupportsBatchUpdates() {
        return true;
    }

    @Override
    public boolean isSupportsBatchSql() {
        return true;
    }
}
