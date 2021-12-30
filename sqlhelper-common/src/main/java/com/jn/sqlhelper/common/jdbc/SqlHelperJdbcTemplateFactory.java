package com.jn.sqlhelper.common.jdbc;

import javax.sql.DataSource;

public class SqlHelperJdbcTemplateFactory implements JdbcTemplateFactory {
    @Override
    public JdbcTemplate get(DataSource dataSource) {
        return new SqlHelperJdbcTemplate(dataSource);
    }
}
