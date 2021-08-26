package com.jn.sqlhelper.dialect;


import com.jn.langx.util.Throwables;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * An implementation of DialectResolutionInfo that delegates calls to a wrapped {@link DatabaseMetaData}.
 * <p/>
 * All {@link SQLException}s resulting from calls on the DatabaseMetaData are converted to the Hibernate
 *
 * @author Steve Ebersole
 */
class DatabaseMetaDataDialectResolutionInfoAdapter implements DialectResolutionInfo {
    private final DatabaseMetaData databaseMetaData;

    public DatabaseMetaDataDialectResolutionInfoAdapter(DatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }

    @Override
    public String getDatabaseProductName() {
        return DialectRegistry.databaseIdString(this.databaseMetaData);
    }

    @Override
    public String getDatabaseProductVersion() {
        try {
            return this.databaseMetaData.getDatabaseProductVersion();
        } catch (SQLException e) {
            throw Throwables.wrapAsRuntimeException(e);
        }
    }

    @Override
    public int getDatabaseMajorVersion() {
        try {
            return interpretVersion(databaseMetaData.getDatabaseMajorVersion());
        } catch (SQLException e) {
            throw Throwables.wrapAsRuntimeException(e);
        }
    }

    private static int interpretVersion(int result) {
        return result < 0 ? NO_VERSION : result;
    }

    @Override
    public int getDatabaseMinorVersion() {
        try {
            return interpretVersion(databaseMetaData.getDatabaseMinorVersion());
        } catch (SQLException e) {
            throw Throwables.wrapAsRuntimeException(e);
        }
    }

    @Override
    public String getDriverName() {
        try {
            return databaseMetaData.getDriverName();
        } catch (SQLException e) {
            throw Throwables.wrapAsRuntimeException(e);
        }
    }

    @Override
    public int getDriverMajorVersion() {
        return interpretVersion(databaseMetaData.getDriverMajorVersion());
    }

    @Override
    public int getDriverMinorVersion() {
        return interpretVersion(databaseMetaData.getDriverMinorVersion());
    }
}
