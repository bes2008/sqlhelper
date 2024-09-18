package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;

import java.util.Locale;

public class GreenplumDialect extends AbstractDialect {
    public GreenplumDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, boolean isSubquery, boolean useLimitVariable, RowSelection rowSelection) {
                //
                /*
                 *
                 reference:  http://gpdb.docs.pivotal.io/5180/ref_guide/sql_commands/SELECT.html
                 Select Syntax:

                 SELECT [ALL | DISTINCT [ON (expression [, ...])]]
                 | expression [[AS] output_name] [, ...]
                 [FROM from_item [, ...]]
                 [WHERE condition]
                 [GROUP BY grouping_element [, ...]]
                 [HAVING condition [, ...]]
                 [WINDOW window_name AS (window_specification)]
                 [{UNION | INTERSECT | EXCEPT} [ALL] select]
                 [ORDER BY expression [ASC | DESC | USING operator] [NULLS {FIRST | LAST}] [, ...]]
                 [LIMIT {count | ALL}]
                 [OFFSET start]
                 [FOR {UPDATE | SHARE} [OF table_name [, ...]] [NOWAIT] [...]]
                 */

                sql = sql.trim();
                String forUpdateClause = null;
                boolean isForUpdate = false;
                int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
                if (forUpdateIndex > -1) {
                    forUpdateClause = sql.substring(forUpdateIndex);
                    sql = sql.substring(0, forUpdateIndex - 1);
                    isForUpdate = true;
                }

                String forShareClause = null;
                boolean isForShare = false;
                if (!isForUpdate) {
                    int forShareIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for share");
                    if (forShareIndex > -1) {
                        forShareClause = sql.substring(forShareIndex);
                        sql = sql.substring(0, forShareIndex - 1);
                        isForShare = true;
                    }
                }

                StringBuilder sql2 = new StringBuilder(sql.length() + 100);
                sql2.append(sql);
                if(useLimitVariable && isUseLimitInVariableMode(isSubquery)){
                    if (rowSelection.hasOffset()) {
                        sql2.append(" LIMIT ? OFFSET ? ");
                    } else {
                        sql2.append(" LIMIT ?  ");
                    }
                }else {
                    int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(rowSelection));
                    int lastRow = getMaxOrLimit(rowSelection);
                    sql2.append(" LIMIT "+ lastRow) ;
                    if (rowSelection.hasOffset()) {
                        sql2.append(" OFFSET " + firstRow);
                    }
                }
                sql2.append(" ");
                if (isForUpdate) {
                    sql2.append(forUpdateClause);
                }
                if (isForShare) {
                    sql2.append(forShareClause);
                }
                return sql2.toString();
            }
        });
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
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }
}
