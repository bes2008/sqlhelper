package com.jn.sqlhelper.springjdbc.spring.boot.autoconfigure;

public class JdbcTemplateNativeProperties {
    /**
     * Number of rows that should be fetched from the database when more rows are
     * needed. Use -1 to use the JDBC driver's default configuration.
     */
    private int fetchSize = -1;

    /**
     * Maximum number of rows. Use -1 to use the JDBC driver's default configuration.
     */
    private int maxRows = -1;

    /**
     * Query timeout. Default is to use the JDBC driver's default configuration. If a
     * duration suffix is not specified, seconds will be used.
     */
    private int queryTimeout = -1;

    public int getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getMaxRows() {
        return this.maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    @Override
    public String toString() {
        return "JdbcTemplateNativeProperties{" +
                "fetchSize=" + fetchSize +
                ", maxRows=" + maxRows +
                ", queryTimeout=" + queryTimeout +
                '}';
    }
}
