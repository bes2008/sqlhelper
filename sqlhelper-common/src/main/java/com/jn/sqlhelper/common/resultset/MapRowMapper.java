package com.jn.sqlhelper.common.resultset;

import com.jn.langx.annotation.Nullable;
import com.jn.sqlhelper.common.utils.LinkedCaseInsensitiveMap;

import java.sql.ResultSet;
import java.util.Map;

public class MapRowMapper implements RowMapper<Map<String, Object>> {
    @Override
    public Map<String, Object> mapping(ResultSet row, int currentRowIndex, ResultSetDescription resultSetDescription) {
        int columnCount = resultSetDescription.getColumnCount();
        Map<String, Object> mapOfColumnValues = createColumnMap(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            String column = resultSetDescription.getColumnName(i);
            mapOfColumnValues.put(getColumnKey(column), getColumnValue(row, i));
        }
        return mapOfColumnValues;
    }

    /**
     * Determine the key to use for the given column in the column Map.
     *
     * @param columnName the column name as returned by the ResultSet
     * @return the column key to use
     * @see java.sql.ResultSetMetaData#getColumnName
     */
    protected String getColumnKey(String columnName) {
        return columnName;
    }

    /**
     * Retrieve a JDBC object value for the specified column.
     * <p>The default implementation uses the {@code getObject} method.
     * Additionally, this implementation includes a "hack" to get around Oracle
     * returning a non standard object for their TIMESTAMP datatype.
     *
     * @param rs    is the ResultSet holding the data
     * @param index is the column index
     * @return the Object returned
     */
    @Nullable
    protected Object getColumnValue(ResultSet rs, int index) throws RuntimeException {
        try {
            return ResultSets.getResultSetValue(rs, index);
        } catch (Throwable ex) {
            throw com.jn.langx.util.Throwables.wrapAsRuntimeException(ex);
        }
    }

    protected Map<String, Object> createColumnMap(int columnCount) {
        return new LinkedCaseInsensitiveMap<Object>(columnCount);
    }
}
