package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.util.ClassLoaders;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

/**
 * http://db.apache.org/derby/docs/10.14/ref/index.html
 */
public class DerbyDialect extends AbstractDialect {
    private int driverVersionMajor;
    private int driverVersionMinor;

    public DerbyDialect() {
        super();
        determineDriverVersion();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
        setPlainSqlScriptParser(new DerbySqlScriptParser());
    }
    public IdentifierCase unquotedIdentifierCase(){
        // ref: https://db.apache.org/derby/docs/10.17/ref/index.html
        return IdentifierCase.UPPER_CASE;
    }
    private void determineDriverVersion() {
        try {
            ClassLoaders.loadClass("org.apache.derby.tools.sysinfo", DerbyDialect.class.getClassLoader());
        } catch (Exception e) {
            this.driverVersionMajor = -1;
            this.driverVersionMinor = -1;
        }
    }

    private boolean isTenPointFiveReleaseOrNewer() {
        return (this.driverVersionMajor > 10) || ((this.driverVersionMajor == 10) && (this.driverVersionMinor >= 5));
    }

    @Override
    public boolean isSupportsLimit() {
        return isTenPointFiveReleaseOrNewer();
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return isTenPointFiveReleaseOrNewer();
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return false;
    }

    private static class DerbySqlScriptParser extends PlainSqlScriptParser{
        @Override
        protected PlainSqlStatementBuilder newSqlStatementBuilder() {
            return new DerbySqlStatementBuilder();
        }
    }

    /**
     * supporting Derby-specific delimiter changes.
     */
    private static class DerbySqlStatementBuilder extends PlainSqlStatementBuilder {
        @Override
        protected String extractAlternateOpenQuote(String token) {
            if (token.startsWith("$$")) {
                return "$$";
            }
            return null;
        }
    }


}
