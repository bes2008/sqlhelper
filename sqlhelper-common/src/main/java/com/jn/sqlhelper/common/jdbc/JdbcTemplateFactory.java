package com.jn.sqlhelper.common.jdbc;

import com.jn.langx.Factory;

import javax.sql.DataSource;

public interface JdbcTemplateFactory extends Factory<DataSource, JdbcTemplate> {
    @Override
    JdbcTemplate get(DataSource dataSource);
}
