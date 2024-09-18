package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.util.regexp.Regexp;
import com.jn.langx.util.regexp.Regexps;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;

public class MonetDialect extends AbstractDialect {
    private static final Regexp SAMPLE_SQL_PATTERN = Regexps.createRegexp(".* sample\\w+(\\d+|\\?)$");

    public MonetDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {

            /**
             * https://www.monetdb.org/Documentation/Manuals/SQLreference/SQLSyntaxOverview#SELECT
             */
            @Override
            public String processSql(String sql, boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
                sql = sql.trim();
                while (sql.endsWith(";")) {
                    sql = sql.substring(0, sql.length() - 1);
                }
                String lowercaseSql = sql.toLowerCase();
                boolean hasSampleClause = false;
                String sampleSql = null;

                if (SAMPLE_SQL_PATTERN.matcher(lowercaseSql).matches()) {
                    hasSampleClause = true;
                    int lastIndex = lowercaseSql.lastIndexOf("sample");
                    sampleSql = " " + sql.substring(lastIndex);
                    sql = sql.substring(0, lastIndex);
                }

                boolean hasOffset = selection.hasOffset();
                if(useLimitVariable && isUseLimitInVariableMode(isSubquery)) {
                    sql = sql + (hasOffset ? " limit ? offset ? " : " limit ? ") ;
                }else{
                    int lastRow = getMaxOrLimit(selection);
                    sql = sql + " limit " + lastRow;
                    if(hasOffset){
                        int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
                        sql = sql + " offset "+ firstRow;
                    }
                }
                sql = sql + (hasSampleClause ? sampleSql : "");
                return sql;
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
