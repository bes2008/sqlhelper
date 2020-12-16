package com.jn.agileway.jdbc.datasource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSources {
    private static final Log logger = LogFactory.getLog(DataSources.class);

    public static final String DATASOURCE_IMPLEMENT_KEY_TOMCAT = "tomcat";
    public static final String DATASOURCE_IMPLEMENT_KEY_HIKARICP = "hikaricp";
    public static final String DATASOURCE_IMPLEMENT_KEY_DRUID = "druid";
    public static final String DATASOURCE_IMPLEMENT_KEY_DBCP2 = "dbcp2";
    public static final String DATASOURCE_IMPLEMENT_KEY_C3P0 = "c3p0";

    public static final String DATASOURCE_IMPLEMENT = "datasource.implementation_key";
    public static final String DATASOURCE_GROUP= "datasource.group";
    public static final String DATASOURCE_NAME = "datasource.name";

    public static final String DATASOURCE_GROUP_DEFAULT ="DEFAULT";

    /**
     * Close the given Connection, obtained from the given DataSource,
     * if it is not managed externally (that is, not bound to the thread).
     * @param con the Connection to close if necessary
     * (if this is {@code null}, the call will be ignored)
     * @param dataSource the DataSource that the Connection was obtained from
     * (may be {@code null})
     */
    public static void releaseConnection(Connection con, DataSource dataSource) {
        try {
            doReleaseConnection(con, dataSource);
        }
        catch (SQLException ex) {
            logger.debug("Could not close JDBC Connection", ex);
        }
        catch (Throwable ex) {
            logger.debug("Unexpected exception on closing JDBC Connection", ex);
        }
    }

    /**
     * Actually close the given Connection, obtained from the given DataSource.
     * Same as {@link #releaseConnection}, but throwing the original SQLException.
     *
     * @param con the Connection to close if necessary
     * (if this is {@code null}, the call will be ignored)
     * @param dataSource the DataSource that the Connection was obtained from
     * (may be {@code null})
     * @throws SQLException if thrown by JDBC methods
     */
    public static void doReleaseConnection(Connection con, DataSource dataSource) throws SQLException {
        if (con == null) {
            return;
        }
        doCloseConnection(con, dataSource);
    }


    /**
     * Close the Connection, unless a {@link SmartDataSource} doesn't want us to.
     * @param con the Connection to close if necessary
     * @param dataSource the DataSource that the Connection was obtained from
     * @throws SQLException if thrown by JDBC methods
     * @see Connection#close()
     * @see SmartDataSource#shouldClose(Connection)
     */
    public static void doCloseConnection(Connection con, DataSource dataSource) throws SQLException {
        if (!(dataSource instanceof SmartDataSource) || ((SmartDataSource) dataSource).shouldClose(con)) {
            con.close();
        }
    }
}
