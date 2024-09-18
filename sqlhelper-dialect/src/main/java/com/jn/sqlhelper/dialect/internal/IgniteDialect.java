package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;

import java.util.Locale;

public class IgniteDialect extends AbstractDialect {
    public IgniteDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
                /**
                 * https://apacheignite-sql.readme.io/docs/select
                 *
                 SELECT Syntax
                 [TOP term] [DISTINCT | ALL] selectExpression [,...]
                 FROM tableExpression [,...] [WHERE expression]
                 [GROUP BY expression [,...]] [HAVING expression]
                 [{UNION [ALL] | MINUS | EXCEPT | INTERSECT} select]
                 [ORDER BY order [,...]]
                 [
                 { LIMIT expression [OFFSET expression]  [SAMPLE_SIZE rowCountInt]} |
                 {[OFFSET expression {ROW | ROWS}] [{FETCH {FIRST | NEXT} expression {ROW | ROWS} ONLY}]}
                 ]
                 */

                sql = sql.trim();
                String forSampleClause = null;
                boolean isForSample = false;
                int forSampleIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("sample_size");
                if (forSampleIndex > -1) {
                    forSampleClause = sql.substring(forSampleIndex);
                    sql = sql.substring(0, forSampleIndex - 1);
                    isForSample = true;
                }
                StringBuilder sql2 = new StringBuilder(sql.length() + 100);
                sql2.append(sql);
                boolean hasOffset = selection.hasOffset();
                if(useLimitVariable && isUseLimitInVariableMode(isSubquery)){
                    if (hasOffset) {
                        sql2.append(" limit ? offset ? ");
                    } else {
                        sql2.append(" limit ? ");
                    }
                }else {
                    int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
                    int lastRow = getMaxOrLimit(selection);
                    if (hasOffset) {
                        sql2.append(" limit "+lastRow+" offset " + firstRow +" ");
                    } else {
                        sql2.append(" limit " + lastRow +" ");
                    }
                }
                if (isForSample) {
                    sql2.append(forSampleClause);
                }

                return sql2.toString();
            }
        });
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }
}
