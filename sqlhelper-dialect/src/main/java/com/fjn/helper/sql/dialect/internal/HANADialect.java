package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;
import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;

import java.io.FilterReader;
import java.io.Reader;


public class HANADialect extends AbstractDialect {
    private static final long serialVersionUID = -379042275442752102L;

    private static class CloseSuppressingReader extends FilterReader {
        protected CloseSuppressingReader(Reader in) {
            super(in);
        }

        @Override
        public void close() {
        }
    }


    public HANADialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, RowSelection selection) {
                boolean hasOffset = LimitHelper.hasFirstRow(selection);
                return getLimitString(sql, hasOffset);
            }

            @Override
            public String getLimitString(String sql, boolean hasOffset) {
                return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
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
}
