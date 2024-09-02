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

package com.jn.sqlhelper.dialect.internal.limit;

import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.util.Locale;


/**
 * select *
 * from xx
 * where ...
 * group by ...
 * order by ...
 * offset ? rows fetch next ? rows only
 * <p>
 * every dialect use the limitHandler should set bindLimitParameterInReverseOrder = false
 *
 *
 * 需要注意的是，offset fetch 通常是在 order by 之后
 */
public class OffsetFetchFirstOnlyLimitHandler extends AbstractLimitHandler {

    @Override
    public String processSql(String sql,boolean isSubquery, boolean useLimitVariable, RowSelection rowSelection) {
        return getLimitString(sql,isSubquery,useLimitVariable, rowSelection.getOffset(), getMaxOrLimit(rowSelection));
    }

    @Override
    protected String getLimitString(String sql,boolean isSubquery, boolean useLimitVariable, long offset, int limit) {
        // https://fmhelp.filemaker.com/docs/16/en/fm16_sql_reference.pdf
        // https://documentation.progress.com/output/ua/OpenEdge_latest/#page/dmsrf%2Foffset-and-fetch-clauses.html%23wwID0E6CPQ
        boolean hasOffset = offset > 0;
        sql = sql.trim();
        String forUpdateClause = "";
        boolean isForUpdate = false;
        String sqlLowercase = sql.toLowerCase(Locale.ROOT);
        if (isSupportForUpdate()) {
            int forUpdateIndex = sqlLowercase.lastIndexOf("for update");
            if (forUpdateIndex > -1) {
                forUpdateClause = sql.substring(forUpdateIndex);
                sql = sql.substring(0, forUpdateIndex - 1);
                isForUpdate = true;
            }
        }


        boolean hasWithClause = false;
        String withClause = null;
        if (isSupportWithInSelectEnd()) {
            int withClauseIndex = sqlLowercase.lastIndexOf("with ", sqlLowercase.length() - (forUpdateClause.length() + 7));
            if (withClauseIndex > 0) {
                sql = sql.substring(0, withClauseIndex - 1);
                hasWithClause = true;
                withClause = sqlLowercase.substring(withClauseIndex);
            }
        }

        boolean hasUsingIndexClause = false;
        String usingIndexClause = null;
        if (isSupportUsingIndexClauseInSelectEnd()) {
            int usingIndexIndex = -1;
            usingIndexIndex = sqlLowercase.lastIndexOf("using index", sqlLowercase.length() - 11);
            if (usingIndexIndex > 0) {
                sql = sql.substring(0, usingIndexIndex - 1);
                hasUsingIndexClause = true;
                usingIndexClause = sqlLowercase.substring(usingIndexIndex);
            }
        }

        StringBuilder sql2 = new StringBuilder(sql.length() + 100);
        sql2.append(sql);

        if (useLimitVariable && getDialect().isUseLimitInVariableMode(isSubquery)) {
            if (hasOffset || !supportSimplifyFirstOnly) {
                sql2.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            } else {
                sql2.append(" FETCH FIRST ? ROWS ONLY");
            }
        } else {
            if (hasOffset || !supportSimplifyFirstOnly) {
                sql2.append(" OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY");
            } else {
                sql2.append(" FETCH FIRST " + limit + " ROWS ONLY");
            }
        }
        if (hasWithClause) {
            sql2.append(withClause);
        } else if (isForUpdate) {
            sql2.append(" " + forUpdateClause);
        } else if (hasUsingIndexClause) {
            sql2.append(" " + usingIndexClause);
        }
        return sql2.toString();
    }

    private boolean isSupportUsingIndexClauseInSelectEnd = false;
    private boolean supportForUpdate = true;
    private boolean supportWithInSelectEnd = true;
    private boolean supportSimplifyFirstOnly = true;
    public boolean isSupportUsingIndexClauseInSelectEnd() {
        return isSupportUsingIndexClauseInSelectEnd;
    }

    public OffsetFetchFirstOnlyLimitHandler setSupportUsingIndexClauseInSelectEnd(boolean supportUsingIndexClauseInSelectEnd) {
        isSupportUsingIndexClauseInSelectEnd = supportUsingIndexClauseInSelectEnd;
        return this;
    }

    public boolean isSupportForUpdate() {
        return supportForUpdate;
    }

    public OffsetFetchFirstOnlyLimitHandler setSupportForUpdate(boolean supportForUpdate) {
        this.supportForUpdate = supportForUpdate;
        return this;
    }

    public boolean isSupportWithInSelectEnd() {
        return supportWithInSelectEnd;
    }

    public OffsetFetchFirstOnlyLimitHandler setSupportWithInSelectEnd(boolean supportWithInSelectEnd) {
        this.supportWithInSelectEnd = supportWithInSelectEnd;
        return this;
    }

    public boolean isSupportSimplifyFirstOnly() {
        return supportSimplifyFirstOnly;
    }

    public OffsetFetchFirstOnlyLimitHandler setSupportSimplifyFirstOnly(boolean supportSimplifyFirstOnly) {
        this.supportSimplifyFirstOnly = supportSimplifyFirstOnly;
        return this;
    }
}
