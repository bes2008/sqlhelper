package com.jn.sqlhelper.dialect.internal.limit;

import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.util.Locale;

public class SkipLimitHandler extends AbstractLimitHandler {
    private String firstNKeyword = "FIRST"; // may be: "LIMIT", "FIRST" or "TOP"

    public SkipLimitHandler() {
        this("FIRST");
    }

    public SkipLimitHandler(String firstNKeyword) {
        this.firstNKeyword = firstNKeyword;
    }

    @Override
    public String processSql(String sql,boolean isSubquery, RowSelection selection) {
        final boolean hasOffset = LimitHelper.hasFirstRow(selection);
        String sqlLimit = "";
        if (hasOffset) {
            if (getDialect().isUseLimitInVariableMode(isSubquery)) {
                sqlLimit = " SKIP ? ";
            } else {
                sqlLimit = " SKIP " + selection.getOffset();
            }
        }
        if (getDialect().isUseLimitInVariableMode(isSubquery)) {
            sqlLimit = sqlLimit + " " + firstNKeyword + " ? ";
        } else {
            sqlLimit = sqlLimit + " " + firstNKeyword + " " + getMaxOrLimit(selection) + " ";
        }

        return new StringBuilder(sql.length() + 10)
                .append(sql)
                .insert(sql.toLowerCase(Locale.ROOT).indexOf("select") + 6, sqlLimit).toString();
    }
}
