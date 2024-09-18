package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.SQLServer2005LimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.TopLimitHandler;
import com.jn.sqlhelper.dialect.urlparser.SqlServerUrlParser;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.scriptfile.SQLServerSqlScriptParser;

/**
 * SQLServer 版本说明
 * <pre>
 *     version      |   Year    |       Release Name
 *     1.0              1989            SQL Server 1.0 (16-bit)
 *     1.1              1991            SQL Server 1.1 (16-bit)
 *     4.2A             1992            SQL Server 4.2A (16-bit)
 *     4.2B             1993            SQL Server 4.2B (16-bit)
 *     4.21a            1993            SQL Server 4.21a
 *     6.0              1995            SQL Server 6.0
 *     6.5              1996            SQL Server 6.5
 *     7.0              1998            SQL Server 7.0
 *     8.0              2000            SQL Server 2000
 *     8.0              2003            SQL Server 2000 64-bit
 *     9.0              2005            SQL Server 2005
 *     10.0             2008            SQL Server 2008
 *     10.25            2010            Azure SQL database
 *     10.50            2010            SQL Server 2008 R2
 *     11.0             2012            SQL Server 2012
 *     12.0             2014            SQL Server 2014
 *     13.0             2016            SQL Server 2016
 *     14.0             2017            SQL Server 2017
 *     15.x             2019            SQL Server 2019
 *     16.x             2022            SQL Server 2022
 * </pre>
 */
@Name("sqlserver")
public class SQLServerDialect extends AbstractTransactSQLDialect {
    private static final int PARAM_LIST_SIZE_LIMIT = 2100;

    public SQLServerDialect() {
        super();
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
        setDelegate(new SQLServer2012Dialect());
    }

    @Override
    public IdentifierCase unquotedIdentifierCase() {
        return IdentifierCase.IGNORE_CASE;
    }

    /**
     * @param productionVersion databaseMeta.getProductionVersion
     */
    public static String guessDatabaseId(String productionVersion) {
        Preconditions.checkNotNull(productionVersion);
        String[] segments = Strings.split(productionVersion, ".");
        if (segments.length > 0) {
            String majorVersionString = segments[0];
            int majorVersion = Integer.parseInt(majorVersionString);
            String databaseId = null;
            switch (majorVersion) {
                case 8:
                    databaseId = "sqlserver2000";
                    break;
                case 9:
                    databaseId = "sqlserver2005";
                    break;
                case 10:
                    databaseId = "sqlserver2008";
                    break;
                case 11:
                    databaseId = "sqlserver2012";
                    break;
                case 12:
                    databaseId = "sqlserver2014";
                    break;
                case 13:
                    databaseId = "sqlserver2016";
                    break;
                case 14:
                    databaseId = "sqlserver2017";
                    break;
                case 15:
                    databaseId = "sqlserver2019";
                    break;
                case 16:
                    databaseId = "sqlserver2022";
                    break;
                default:
                    break;
            }
            return databaseId;
        }
        return null;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return false;
    }

    @Name("sqlserver2000")
    public static class SQLServer2000Dialect extends AbstractTransactSQLDialect {
        public SQLServer2000Dialect() {
            setLimitHandler(new TopLimitHandler());
            setDelegate(null);
        }

        @Override
        public IdentifierCase unquotedIdentifierCase() {
            return IdentifierCase.IGNORE_CASE;
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
            return true;
        }
    }

    @Name("sqlserver2005")
    public static class SQLServer2005Dialect extends AbstractTransactSQLDialect {
        public SQLServer2005Dialect() {
            setLimitHandler(new SQLServer2005LimitHandler());
            setDelegate(null);
        }

        @Override
        public IdentifierCase unquotedIdentifierCase() {
            return IdentifierCase.IGNORE_CASE;
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

        @Override
        public char getBeforeQuote() {
            return '[';
        }

        @Override
        public char getAfterQuote() {
            return ']';
        }
    }

    @Name("sqlserver2008")
    public static class SQLServer2008Dialect extends SQLServer2005Dialect {
    }

    @Name("sqlserver2012")
    public static class SQLServer2012Dialect extends SQLServer2008Dialect {
        public SQLServer2012Dialect() {
            setLimitHandler(new OffsetFetchFirstOnlyLimitHandler().setSupportSimplifyFirstOnly(false));
        }

        @Override
        public boolean isForceLimitUsage() {
            return true;
        }

        @Override
        public boolean isBindLimitParametersInReverseOrder() {
            return false;
        }
    }

    @Name("sqlserver2014")
    public static class SQLServer2014Dialect extends SQLServer2012Dialect {
    }

    @Name("sqlserver2016")
    public static class SQLServer2016Dialect extends SQLServer2014Dialect {
    }

    @Name("sqlserver2017")
    public static class SQLServer2017Dialect extends SQLServer2016Dialect {
    }
    @Name("sqlserver2019")
    public static class SQLServer2019Dialect extends SQLServer2017Dialect {
    }
    @Name("sqlserver2022")
    public static class SQLServer2022Dialect extends SQLServer2019Dialect {
    }

    @Override
    public char getBeforeQuote() {
        return '[';
    }

    @Override
    public char getAfterQuote() {
        return ']';
    }





}
