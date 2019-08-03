package com.jn.sqlhelper.mango;

import com.jn.sqlhelper.dialect.RowSelection;
import org.jfaster.mango.binding.BoundSql;
import org.jfaster.mango.jdbc.JdbcTemplate;
import org.jfaster.mango.jdbc.ResultSetExtractor;
import org.jfaster.mango.jdbc.exception.DataAccessException;
import org.jfaster.mango.transaction.DataSourceUtils;
import org.jfaster.mango.type.TypeHandler;
import org.jfaster.mango.util.logging.InternalLogger;
import org.jfaster.mango.util.logging.InternalLoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class CustomMangoJdbcTemplate extends JdbcTemplate {
    private final static InternalLogger logger = InternalLoggerFactory.getInstance(JdbcTemplate.class);

    protected <T> T executeQuery(DataSource dataSource, BoundSql boundSql, ResultSetExtractor<T> rse)
            throws DataAccessException {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = boundSql.getSql();
        try {
            ps = conn.prepareStatement(sql);
            setValues(ps, boundSql);

            if (logger.isDebugEnabled()) {
                logger.debug("Executing \"{}\" {}", sql, boundSql.getArgs());
            }

            rs = ps.executeQuery();
            return rse.extractData(rs);
        } catch (SQLException e) {
            closeResultSet(rs);
            rs = null;
            closeStatement(ps);
            ps = null;
            DataSourceUtils.releaseConnection(conn, dataSource);
            conn = null;

            throw new DataAccessException("Execute sql [" + sql + "] error.", e) {
            };
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    private void setValues(PreparedStatement ps, BoundSql boundSql) throws SQLException {
        RowSelection rowSelection = MangoPagingContext.pagingRequest.get();
        if (rowSelection != null) {
            MangoPagingContext.pagingRequest.remove();
            MangoQueryParameters queryParameters = new MangoQueryParameters();
            queryParameters.setRowSelection(rowSelection);
            queryParameters.setParameters(boundSql);
            MangoPagingContext.instrumentor.bindParameters(ps, new MangoPrepareStatementSetter(), queryParameters, true);
        } else {
            setValues0(ps, boundSql);
        }
    }

    private void setValues0(PreparedStatement ps, BoundSql boundSql) throws SQLException {
        List<Object> args = boundSql.getArgs();
        List<TypeHandler<?>> typeHandlers = boundSql.getTypeHandlers();
        for (int i = 0; i < args.size(); i++) {
            TypeHandler typeHandler;
            typeHandler = typeHandlers.get(i);
            Object value = args.get(i);
            typeHandler.setParameter(ps, i + 1, value);
        }
    }

    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("Could not close JDBC ResultSet", e);
            } catch (Throwable e) {
                logger.error("Unexpected exception on closing JDBC ResultSet", e);
            }
        }
    }

    private void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.error("Could not close JDBC Statement", e);
            } catch (Throwable e) {
                logger.error("Unexpected exception on closing JDBC Statement", e);
            }
        }
    }
}
