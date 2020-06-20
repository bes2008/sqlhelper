package com.jn.sqlhelper.common.resultset;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdatedRowsResultSetExtractor implements ResultSetExtractor<Integer> {
    public static final UpdatedRowsResultSetExtractor INSTANCE = new UpdatedRowsResultSetExtractor();

    @Override
    public Integer extract(ResultSet rs) throws SQLException {
        if (rs.next() && rs.getMetaData().getColumnCount() > 0) {
            return rs.getInt(1);
        } else {
            return 0;
        }
    }
}
