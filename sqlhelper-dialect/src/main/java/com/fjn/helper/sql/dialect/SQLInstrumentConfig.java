package com.fjn.helper.sql.dialect;

public class SQLInstrumentConfig {
    private String dialect;
    private String dialectClassName;

    public String getDialect() {
        
        return this.dialect;
    }

    public void setDialect(final String dialect) {
        
        this.dialect = dialect;
    }

    public String getDialectClassName() {
        
        return this.dialectClassName;
    }

    public void setDialectClassName(final String dialectClassName) {
        
        this.dialectClassName = dialectClassName;
    }
}
