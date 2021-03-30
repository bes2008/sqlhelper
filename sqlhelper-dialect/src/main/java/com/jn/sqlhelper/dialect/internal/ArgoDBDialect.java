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

import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;

/**
 * 星环科技开发：https://docs.transwarp.cn/#/documents-support/docs-detail/document/ARGODB-PLATFORM/2.1/030ArgoDBManual?docType=docs%3Fcategory%3DTDH%26index%3D0&docName=ArgoDB%E4%BD%BF%E7%94%A8%E6%89%8B%E5%86%8C
 *
 * ArgoDB支持MySQL和DB2中的分页语法。
 * <p>
 * SELECT [ALL | DISTINCT] select_expression, select_expression, ...
 * FROM table_reference
 * [WHERE where_condition]
 * [GROUP BY col_list]
 * [CLUSTER BY col_list
 * | [DISTRIBUTE BY col_list] [SORT BY col_list]
 * ]
 * [LIMIT (M,)N
 * | [OFFSET M ROWS FETCH NEXT | FIRST] N ROWS ONLY];
 */
public class ArgoDBDialect extends AbstractDialect {
    public ArgoDBDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
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
    public boolean isSupportsBatchUpdates() {
        return true;
    }

    @Override
    public boolean isSupportsBatchSql() {
        return true;
    }

}
