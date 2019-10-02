package com.jn.sqlhelper.common.resultset;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.ddlmodel.ResultSetDescription;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {
    private int offset = 0;
    private int expectedMaxRows = Integer.MAX_VALUE;
    private RowMapper<T> mapper;

    public RowMapperResultSetExtractor(RowMapper<T> rowMapper) {
        this(rowMapper, 0, Integer.MAX_VALUE);
    }

    public RowMapperResultSetExtractor(RowMapper<T> rowMapper, int expectedMaxRows) {
        this(rowMapper, 0, expectedMaxRows);
    }

    public RowMapperResultSetExtractor(RowMapper<T> rowMapper, int offset, int expectedMaxRows) {
        Preconditions.checkNotNull(rowMapper, "RowMapper is required");
        Preconditions.checkTrue(offset >= 0, "offset is zero at least");
        this.offset = offset;
        this.mapper = rowMapper;
        this.expectedMaxRows = expectedMaxRows;
        if (expectedMaxRows < 0) {
            this.expectedMaxRows = 0;
        }
    }


    @Override
    public List<T> extract(ResultSet rs) throws SQLException {
        List<T> results = (this.expectedMaxRows > 0 ? new ArrayList<T>(16) : new ArrayList<T>());
        int rowIndex = 0;

        if (expectedMaxRows > 0) {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            ResultSetDescription resultSetDescription = new ResultSetDescription(rsMetaData);
            while (rs.next() && results.size() < expectedMaxRows) {
                if (rowIndex < offset) {
                    rowIndex++;
                    continue;
                }
                results.add(this.mapper.mapping(rs, rowIndex++, resultSetDescription));
            }
        }
        return results;
    }
}
