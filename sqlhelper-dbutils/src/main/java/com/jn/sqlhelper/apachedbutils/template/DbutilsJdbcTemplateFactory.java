package com.jn.sqlhelper.apachedbutils.template;

import com.jn.sqlhelper.apachedbutils.QueryRunner;
import com.jn.sqlhelper.common.jdbc.JdbcTemplate;
import com.jn.sqlhelper.common.jdbc.JdbcTemplateFactory;

import javax.sql.DataSource;

public class DbutilsJdbcTemplateFactory implements JdbcTemplateFactory {
    @Override
    public JdbcTemplate get(DataSource dataSource) {
        QueryRunner runner = new QueryRunner(dataSource);
        return new DbutilsJdbcTemplate(runner);
    }
}
