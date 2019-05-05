package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;
import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;
import com.fjn.helper.sql.dialect.internal.limit.SQLServer2005LimitHandler;
import com.fjn.helper.sql.dialect.internal.limit.TopLimitHandler;
import com.fjn.helper.sql.dialect.internal.urlparser.SqlServerUrlParser;

import java.util.Locale;


public class SQLServerDialect extends AbstractTransactSQLDialect {
    private static final int PARAM_LIST_SIZE_LIMIT = 2100;

    public SQLServerDialect() {
        super();
        setUrlParser(new SqlServerUrlParser());
        setDelegate(new SQLServer2008Dialect());
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return false;
    }

    class SQLServer2000Dialect extends AbstractTransactSQLDialect {
        private SQLServer2000Dialect() {
            setLimitHandler(new TopLimitHandler() {
                @Override
                public String getLimitString(String querySelect, int offset, int limit) {
                    if (offset > 0) {
                        throw new UnsupportedOperationException("query result offset is not supported");
                    }


                    return new StringBuilder(querySelect.length() + 8).append(querySelect).insert(getAfterSelectInsertPoint(querySelect), " top " + limit).toString();
                }

                private int getAfterSelectInsertPoint(String sql) {
                    int selectIndex = sql.toLowerCase(Locale.ROOT).indexOf("select");
                    int selectDistinctIndex = sql.toLowerCase(Locale.ROOT).indexOf("select distinct");
                    return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
                }
            });
        }

        @Override
        public boolean isSupportsLimit() {
            return true;
        }

        @Override
        public boolean isUseMaxForLimit() {
            return true;
        }

        @Override
        public boolean isSupportsLimitOffset() {
            return false;
        }

        @Override
        public boolean isSupportsVariableLimit() {
            return false;
        }

        @Override
        public boolean isBindLimitParametersFirst() {
            return false;
        }
    }

    class SQLServer2005Dialect extends AbstractTransactSQLDialect {
        public SQLServer2005Dialect() {
            setLimitHandler(new SQLServer2005LimitHandler());
        }

        @Override
        public boolean isSupportsLimit() {
            return true;
        }

        @Override
        public boolean isUseMaxForLimit() {
            return true;
        }

        @Override
        public boolean isSupportsLimitOffset() {
            return true;
        }

        @Override
        public boolean isSupportsVariableLimit() {
            return true;
        }
    }

    class SQLServer2008Dialect extends AbstractTransactSQLDialect {
        public SQLServer2008Dialect() {
            setLimitHandler(new AbstractLimitHandler() {
                @Override
                public String processSql(String sql, RowSelection selection) {
                    if (LimitHelper.useLimit(getDialect(), selection)) {
                        return sql + (LimitHelper.hasFirstRow(selection) ? " offset ? rows fetch next ? rows only" : " fetch first ? rows only");
                    }


                    return sql;
                }
            });
        }

        @Override
        public boolean isSupportsLimit() {
            return true;
        }

        @Override
        public boolean isUseMaxForLimit() {
            return true;
        }

        @Override
        public boolean isSupportsLimitOffset() {
            return true;
        }

        @Override
        public boolean isSupportsVariableLimit() {
            return true;
        }
    }
}
