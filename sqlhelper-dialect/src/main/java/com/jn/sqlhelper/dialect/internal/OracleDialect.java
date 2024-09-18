package com.jn.sqlhelper.dialect.internal;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.SQLDialectException;
import com.jn.sqlhelper.dialect.annotation.Driver;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHelper;
import com.jn.sqlhelper.dialect.internal.limit.OracleXLimitHandler;
import com.jn.sqlhelper.dialect.urlparser.OracleUrlParser;
import com.jn.sqlhelper.dialect.scriptfile.OracleSqlScriptParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Locale;


@Driver({"oracle.jdbc.OracleDriver","oracle.jdbc.driver.OracleDriver"})
public class OracleDialect extends AbstractDialect {
    private static final int PARAM_LIST_SIZE_LIMIT = 1000;

    public OracleDialect() {
        super();
        setDelegate(new Oracle9iDialect());
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
    }

    public OracleDialect(java.sql.Driver driver) {
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);

        int majorVersion = driver.getMajorVersion();
        int minorVersion = driver.getMinorVersion();
        if (majorVersion < 9) {
            setDelegate(new Oracle8iDialect());
            return;
        }
        if (majorVersion == 9) {
            setDelegate(new Oracle9iDialect());
            return;
        }
        if (majorVersion == 10) {
            if (minorVersion < 3) {
                setDelegate(new Oracle10gDialect());
                return;
            }
            setDelegate(new Oracle11gDialect());
            return;
        }

        if (majorVersion >= 12) {
            setDelegate(new Oracle12cDialect());
            return;
        }
        setDelegate(new Oracle9Dialect());

    }

    class OracleBaseDialect extends AbstractDialect {
        OracleBaseDialect() {
            setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
        }

        @Override
        public IdentifierCase unquotedIdentifierCase() {
            return IdentifierCase.UPPER_CASE;
        }

        @Override
        public boolean isSupportsLimit() {
            return true;
        }

        @Override
        public boolean isBindLimitParametersInReverseOrder() {
            return true;
        }

        @Override
        public boolean isUseMaxForLimit() {
            return true;
        }

        @Override
        public int registerResultSetOutParameter(CallableStatement statement, int col)
                throws SQLException {
            statement.registerOutParameter(col, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType());
            col++;
            return col;
        }

        @Override
        public boolean isSupportsBatchUpdates() {
            return true;
        }

        @Override
        public boolean isSupportsBatchSql() {
            return true;
        }
    }

    private static class Oracle8i9LimitHandler extends AbstractLimitHandler {
        @Override
        public String processSql(String sql,boolean isSubquery, boolean useLimitVariable, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            sql = sql.trim();
            boolean isForUpdate = false;
            if (sql.toLowerCase(Locale.ROOT).endsWith(" for update")) {
                sql = sql.substring(0, sql.length() - 11);
                isForUpdate = true;
            }

            StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
            if (hasOffset) {
                pagingSelect.append("select * from ( select sqlhelper_rowtable_.*, rownum rownum_ from ( ");
            } else {
                pagingSelect.append("select * from ( ");
            }
            pagingSelect.append(sql);

            if(useLimitVariable && this.getDialect().isUseLimitInVariableMode(isSubquery)){
                if (hasOffset) {
                    pagingSelect.append(" ) sqlhelper_rowtable_ ) where rownum_ <= ? and rownum_ > ?");
                } else {
                    pagingSelect.append(" ) sqlhelper_rowtable_ where rownum <= ?");
                }
            }else{
                int firstRow = (int)convertToFirstRowValue(LimitHelper.getFirstRow(selection));
                int lastRow = getMaxOrLimit(selection);
                if (hasOffset) {
                    pagingSelect.append(" ) sqlhelper_rowtable_ ) where rownum_ <= "+lastRow+" and rownum_ > " + firstRow +" ");
                } else {
                    pagingSelect.append(" ) sqlhelper_rowtable_ where rownum <= "+lastRow+" ");
                }
            }


            if (isForUpdate) {
                pagingSelect.append(" for update");
            }

            return pagingSelect.toString();
        }
    }

    private class Oracle8iDialect extends OracleBaseDialect {
        private Oracle8iDialect() {
            super();
            setLimitHandler(new Oracle8i9LimitHandler());
        }
    }

    private class Oracle9Dialect extends OracleBaseDialect {
        private Oracle9Dialect() {
            super();
            setLimitHandler(new Oracle8i9LimitHandler());
        }
    }

    private class Oracle9iDialect extends OracleBaseDialect {
        private Oracle9iDialect() {
            super();
            setLimitHandler(new OracleXLimitHandler());
        }
    }

    private class Oracle10gDialect extends Oracle9iDialect {
        private Oracle10gDialect() {
            super();
        }
    }

    private class Oracle11gDialect extends Oracle10gDialect {
        private Oracle11gDialect() {
            super();
        }
    }

    private class Oracle12cDialect extends Oracle11gDialect {
        private Oracle12cDialect() {
            super();
        }
    }

    private static class OracleTypesHelper {
        private static final Logger log = LoggerFactory.getLogger(OracleTypesHelper.class);


        public static final OracleTypesHelper INSTANCE = new OracleTypesHelper();

        private static final String ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.OracleTypes";
        private static final String DEPRECATED_ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.driver.OracleTypes";
        private final int oracleCursorTypeSqlType;

        private OracleTypesHelper() {
            int typeCode = -99;
            try {
                typeCode = extractOracleCursorTypeValue();
            } catch (Exception e) {
                log.warn("Unable to resolve Oracle CURSOR JDBC type code", e);
            }
            this.oracleCursorTypeSqlType = typeCode;
        }

        private int extractOracleCursorTypeValue() {
            try {
                return locateOracleTypesClass().getField("CURSOR").getInt(null);
            } catch (Exception se) {
                throw new SQLDialectException("Unable to access OracleTypes.CURSOR value", se);
            }
        }

        private Class locateOracleTypesClass() {
            try {
                return Class.forName("oracle.jdbc.OracleTypes");
            } catch (ClassNotFoundException e) {
                try {
                    return Class.forName("oracle.jdbc.driver.OracleTypes");
                } catch (ClassNotFoundException e2) {
                    throw new SQLDialectException(String.format("Unable to locate OracleTypes class using either known FQN [%s, %s]", new Object[]{"oracle.jdbc.OracleTypes", "oracle.jdbc.driver.OracleTypes"}), e);
                }
            }
        }


        public int getOracleCursorTypeSqlType() {
            return this.oracleCursorTypeSqlType;
        }
    }



}
