package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;

/**
 * http://www.firebirdsql.org/file/documentation/reference_manuals/fblangref25-en/html/fblangref25-dml-select.html#fblangref25-dml-select-first-skip
 * http://www.firebirdsql.org/file/documentation/reference_manuals/fblangref25-en/html/fblangref25-commons-predicates.html
 */
public class FirebirdDialect extends InterbaseDialect {

    public FirebirdDialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, boolean isSubQuery, boolean useLimitVariable, RowSelection selection) {
                boolean hasOffset = LimitHelper.hasFirstRow(selection);
                sql = sql.trim();
                StringBuilder sqlbuiler = new StringBuilder(sql.length() + 20).append(sql);

                String substring = "";
                if(useLimitVariable && isUseLimitInVariableMode(isSubQuery)){
                    substring = hasOffset ? " first ? skip ?" : " first ?";
                }else{
                    int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
                    int lastRow = getMaxOrLimit(selection);
                    substring = " first "+lastRow ;
                    if(hasOffset){
                        substring = " skip "+ firstRow +" ";
                    }
                }
                return sqlbuiler.insert(6, substring).toString();
            }
        });

    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }
}
