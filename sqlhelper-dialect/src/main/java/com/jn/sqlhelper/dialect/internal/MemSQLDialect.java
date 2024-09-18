package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;

import java.util.Locale;

public class MemSQLDialect extends AbstractDialect {
    public MemSQLDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
                /*
                 *
                 *
                    reference: https://docs.memsql.com/sql-reference/v6.8/select/
                 Select Syntax:

                 SELECT
                 [ALL | DISTINCT | DISTINCTROW]
                 select_expr [[AS] alias], ...
                 [FROM table_references
                 [WHERE expr]
                 [GROUP BY {{col_name | expr | position}, ...
                 | extended_grouping_expr}]
                 [HAVING expr]
                 [ORDER BY {col_name | expr | position} [ASC | DESC], ...]
                 [LIMIT {[offset,] row_count | row_count OFFSET offset}]
                 [INTO OUTFILE 'file_name' export_options]
                 [INTO S3 bucket/target CONFIG configuration_json CREDENTIALS credentials_json]
                 [FOR UPDATE]
                 ]
                 */

                sql = sql.trim();
                String intoOutfileClause = null;
                boolean isIntoOutfile = false;
                int intoOutfileIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("into outfile");
                if (intoOutfileIndex > -1) {
                    intoOutfileClause = sql.substring(intoOutfileIndex);
                    sql = sql.substring(0, intoOutfileIndex - 1);
                    isIntoOutfile = true;
                }

                String intoClause = null;
                boolean isInto = false;
                if (!isIntoOutfile) {
                    int intoIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("into");
                    if (intoIndex > -1) {
                        intoClause = sql.substring(intoIndex);
                        sql = sql.substring(0, intoIndex - 1);
                        isInto = true;
                    }
                }

                String forUpdateClause = null;
                boolean isForUpdate = false;
                if (!isInto) {
                    int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
                    if (forUpdateIndex > -1) {
                        forUpdateClause = sql.substring(forUpdateIndex);
                        sql = sql.substring(0, forUpdateIndex - 1);
                        isForUpdate = true;
                    }
                }

                StringBuilder sql2 = new StringBuilder(sql.length() + 100);
                sql2.append(sql);

                // its limit clause supports two styles:
                // 1) LIMIT $offset, $limit
                // 2) LIMIT $limit OFFSET $offset

                // we use 2)
                if(useLimitVariable && isUseLimitInVariableMode(isSubquery)) {
                    if (selection.hasOffset()) {
                        sql2.append(" limit ? OFFSET ? ");
                    } else {
                        sql2.append(" limit ? ");
                    }
                }else{
                    int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
                    int lastRow = getMaxOrLimit(selection);
                    sql2.append(" limit " + lastRow +" ");
                    if (selection.hasOffset()) {
                        sql2.append(" OFFSET " + firstRow);
                    }
                }

                if (isIntoOutfile) {
                    sql2.append(intoOutfileClause);
                } else if (isInto) {
                    sql2.append(intoClause);
                } else if (isForUpdate) {
                    sql2.append(forUpdateClause);
                }
                return sql2.toString();
            }
        });
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }
}
