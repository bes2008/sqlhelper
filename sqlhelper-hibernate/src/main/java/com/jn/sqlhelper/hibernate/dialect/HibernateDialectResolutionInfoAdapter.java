package com.jn.sqlhelper.hibernate.dialect;

import com.jn.sqlhelper.dialect.DialectResolutionInfo;

class HibernateDialectResolutionInfoAdapter implements DialectResolutionInfo {
    private org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo hibernate;

    HibernateDialectResolutionInfoAdapter(org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo hibernate) {
        this.hibernate = hibernate;
    }

    @Override
    public String getDatabaseProductName() {
        return hibernate.getDatabaseName();
    }

    @Override
    public String getDatabaseProductVersion() {
        return hibernate.getDatabaseMajorVersion() + "";
    }

    @Override
    public int getDatabaseMajorVersion() {
        return hibernate.getDatabaseMajorVersion();
    }

    @Override
    public int getDatabaseMinorVersion() {
        return hibernate.getDatabaseMinorVersion();
    }

    @Override
    public String getDriverName() {
        return hibernate.getDriverName();
    }

    @Override
    public int getDriverMajorVersion() {
        return hibernate.getDriverMajorVersion();
    }

    @Override
    public int getDriverMinorVersion() {
        return hibernate.getDriverMinorVersion();
    }
}
