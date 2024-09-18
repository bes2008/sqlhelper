package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;
import com.jn.sqlhelper.dialect.internal.limit.LimitOnlyLimitHandler;
import com.jn.sqlhelper.dialect.pagination.RowSelection;

/**
 * https://www.alibabacloud.com/help/doc-detail/73777.htm?spm=a2c63.p38356.b99.86.72a82f4dUDIiYI
 */
public class MaxComputeDialect extends AbstractDialect {
    public MaxComputeDialect() {
        super();
        setLimitHandler(new LimitOnlyLimitHandler());
    }

    private static class MaxComputeLimitHandler extends AbstractLimitHandler {
        @Override
        public String processSql(String sql,boolean isSubquery, boolean useLimitVariable, RowSelection rowSelection) {
            return getLimitString(sql,isSubquery, useLimitVariable, LimitHelper.getFirstRow(rowSelection), getMaxOrLimit(rowSelection));
        }

        @Override
        protected String getLimitString(String sql,boolean isSubquery, boolean useLimitVariable, long offset, int limit) {
            if (offset == 0) {
                if (useLimitVariable && getDialect().isUseLimitInVariableMode(isSubquery)) {
                    return sql + " limit ?";
                } else {
                    return sql + " limit " + limit;
                }
            } else {
                StringBuilder sqlBuilder = new StringBuilder(sql.length() + 256);
                sqlBuilder.append("select * from ( select row_number() over() as sqlhelper_ROW_ID, * from (")
                        .append(sql)
                        .append(" ) ) sqlhelper_tmp  where sqlhelper_ROW_ID ");
                if (useLimitVariable && getDialect().isUseLimitInVariableMode(isSubquery)) {
                    sqlBuilder.append(" between ? and ? ");
                } else {
                    int firstRow = (int)convertToFirstRowValue(offset);
                    int lastRow = getDialect().isUseMaxForLimit() ? (limit + (int)firstRow) : limit;
                    sqlBuilder.append(" between "+firstRow+" and "+ lastRow);
                }
                return sqlBuilder.toString();
            }
        }
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }
}
