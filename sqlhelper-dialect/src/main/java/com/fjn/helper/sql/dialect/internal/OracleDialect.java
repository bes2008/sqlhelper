package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;
import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.SQLDialectException;
import com.fjn.helper.sql.dialect.annotation.Driver;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;
import com.fjn.helper.sql.dialect.internal.limit.DefaultLimitHandler;
import com.fjn.helper.sql.dialect.internal.urlparser.OracleUrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Locale;


@Driver("oracle.jdbc.driver.OracleDriver")
public class OracleDialect extends AbstractDialect {
    private static final int PARAM_LIST_SIZE_LIMIT = 1000;

    public OracleDialect() {
        super();
        setUrlParser(new OracleUrlParser());
        setDelegate(new Oracle9iDialect());
    }

    public OracleDialect(java.sql.Driver driver) {
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
    }

    private class Oracle8iDialect extends OracleBaseDialect {
        private Oracle8iDialect() {
            super();
            setLimitHandler(new AbstractLimitHandler() {
                @Override
                public String processSql(String sql, RowSelection selection) {
                    boolean hasOffset = LimitHelper.hasFirstRow(selection);
                    return getLimitString(sql, hasOffset);
                }

                @Override
                public String getLimitString(String sql, boolean hasOffset) {
                    sql = sql.trim();
                    boolean isForUpdate = false;
                    if (sql.toLowerCase(Locale.ROOT).endsWith(" for update")) {
                        sql = sql.substring(0, sql.length() - 11);
                        isForUpdate = true;
                    }

                    StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
                    if (hasOffset) {
                        pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
                    } else {
                        pagingSelect.append("select * from ( ");
                    }
                    pagingSelect.append(sql);
                    if (hasOffset) {
                        pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
                    } else {
                        pagingSelect.append(" ) where rownum <= ?");
                    }

                    if (isForUpdate) {
                        pagingSelect.append(" for update");
                    }

                    return pagingSelect.toString();
                }
            });
        }
    }

    private class Oracle9Dialect extends OracleBaseDialect {
        private Oracle9Dialect() {
            super();
            setLimitHandler(new DefaultLimitHandler(this) {
                @Override
                public String getLimitString(String sql, boolean hasOffset) {
                    sql = sql.trim();
                    boolean isForUpdate = false;
                    if (sql.toLowerCase(Locale.ROOT).endsWith(" for update")) {
                        sql = sql.substring(0, sql.length() - 11);
                        isForUpdate = true;
                    }

                    StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
                    if (hasOffset) {
                        pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
                    } else {
                        pagingSelect.append("select * from ( ");
                    }
                    pagingSelect.append(sql);
                    if (hasOffset) {
                        pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
                    } else {
                        pagingSelect.append(" ) where rownum <= ?");
                    }

                    if (isForUpdate) {
                        pagingSelect.append(" for update");
                    }

                    return pagingSelect.toString();
                }
            });
        }
    }

    private class Oracle9iDialect extends OracleBaseDialect {
        private Oracle9iDialect() {
            super();
            setLimitHandler(new AbstractLimitHandler() {
                @Override
                public String processSql(String sql, RowSelection selection) {
                    boolean hasOffset = LimitHelper.hasFirstRow(selection);
                    return getLimitString(sql, hasOffset);
                }

                @Override
                public String getLimitString(String sql, boolean hasOffset) {
                    sql = sql.trim();
                    String forUpdateClause = null;
                    boolean isForUpdate = false;
                    int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
                    if (forUpdateIndex > -1) {
                        forUpdateClause = sql.substring(forUpdateIndex);
                        sql = sql.substring(0, forUpdateIndex - 1);
                        isForUpdate = true;
                    }

                    StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
                    if (hasOffset) {
                        pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
                    } else {
                        pagingSelect.append("select * from ( ");
                    }
                    pagingSelect.append(sql);
                    if (hasOffset) {
                        pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
                    } else {
                        pagingSelect.append(" ) where rownum <= ?");
                    }

                    if (isForUpdate) {
                        pagingSelect.append(" ");
                        pagingSelect.append(forUpdateClause);
                    }

                    return pagingSelect.toString();
                }
            });
        }
    }

    private class Oracle10gDialect
            extends Oracle9iDialect {
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
