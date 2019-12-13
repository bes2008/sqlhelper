/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.dialect.pagination;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

public class PagedPreparedStatement implements PreparedStatement {
    private PreparedStatement delegate;

    private int indexOffset = -1; // -1 disable offset
    private final Set<Integer> setParameterIndexes = new LinkedHashSet<Integer>();

    public Set<Integer> getSetParameterIndexes() {
        return this.setParameterIndexes;
    }

    public void setIndexOffset(int indexOffset) {
        this.indexOffset = indexOffset;
    }

    public PagedPreparedStatement(PreparedStatement delegate) {
        this.delegate = delegate;
    }

    private void recordParameterSet(int index) {
        if (indexOffset >= 0) {
            setParameterIndexes.add(index);
        }
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return delegate.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        return delegate.executeUpdate();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setNull(parameterIndex, sqlType);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setBoolean(parameterIndex, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setByte(parameterIndex, x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setShort(parameterIndex, x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setInt(parameterIndex, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setLong(parameterIndex, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setFloat(parameterIndex, x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setDouble(parameterIndex, x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setBigDecimal(parameterIndex, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setString(parameterIndex, x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setBytes(parameterIndex, x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setDate(parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setTime(parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setTimestamp(parameterIndex, x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setUnicodeStream(parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void clearParameters() throws SQLException {
        delegate.clearParameters();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setObject(parameterIndex, x, targetSqlType);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setObject(parameterIndex, x);
    }

    @Override
    public boolean execute() throws SQLException {
        return delegate.execute();
    }

    @Override
    public void addBatch() throws SQLException {
        delegate.addBatch();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setRef(parameterIndex, x);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setBlob(parameterIndex, x);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setClob(parameterIndex, x);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setArray(parameterIndex, x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return delegate.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setDate(parameterIndex, x, cal);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setTime(parameterIndex, x, cal);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setTimestamp(parameterIndex, x, cal);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setNull(parameterIndex, sqlType, typeName);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setURL(parameterIndex, x);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return delegate.getParameterMetaData();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setRowId(parameterIndex, x);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setNString(parameterIndex, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setNCharacterStream(parameterIndex, value, length);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setNClob(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setClob(parameterIndex, reader, length);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setBlob(parameterIndex, inputStream, length);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setNClob(parameterIndex, reader, length);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setSQLXML(parameterIndex, xmlObject);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setAsciiStream(parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setBinaryStream(parameterIndex, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setCharacterStream(parameterIndex, reader);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setNCharacterStream(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setClob(parameterIndex, reader);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setBlob(parameterIndex, inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setNClob(parameterIndex, reader);
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return delegate.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return delegate.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        delegate.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return delegate.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        delegate.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return delegate.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        delegate.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        delegate.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return delegate.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        delegate.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        delegate.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return delegate.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        delegate.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        delegate.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return delegate.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return delegate.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return delegate.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return delegate.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        delegate.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return delegate.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        delegate.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return delegate.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return delegate.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return delegate.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        delegate.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        delegate.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return delegate.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return delegate.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return delegate.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return delegate.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return delegate.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return delegate.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return delegate.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return delegate.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return delegate.execute(sql, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return delegate.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return delegate.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        delegate.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return delegate.isPoolable();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegate.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegate.isWrapperFor(iface);
    }

    public void closeOnCompletion() throws SQLException {
        delegate.closeOnCompletion();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        return delegate.isCloseOnCompletion();
    }

    public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        if (indexOffset >= 0) {
            parameterIndex = parameterIndex + indexOffset;
            recordParameterSet(parameterIndex);
        }
        delegate.setObject(parameterIndex, x, targetSqlType);
    }

    public long executeLargeUpdate() throws SQLException {
        return delegate.executeLargeUpdate();
    }

    public long getLargeUpdateCount() throws SQLException {
        return delegate.getLargeUpdateCount();
    }

    public void setLargeMaxRows(long max) throws SQLException {
        delegate.setLargeMaxRows(max);
    }

    public long getLargeMaxRows() throws SQLException {
        return delegate.getLargeMaxRows();
    }

    public long[] executeLargeBatch() throws SQLException {
        return delegate.executeLargeBatch();
    }

    public long executeLargeUpdate(String sql) throws SQLException {
        return delegate.executeLargeUpdate();
    }

    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return delegate.executeLargeUpdate(sql, autoGeneratedKeys);
    }

    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return delegate.executeLargeUpdate(sql, columnIndexes);
    }

    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        return delegate.executeLargeUpdate(sql, columnNames);
    }
}
