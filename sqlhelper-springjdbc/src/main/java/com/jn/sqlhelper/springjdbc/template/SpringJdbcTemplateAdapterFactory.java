package com.jn.sqlhelper.springjdbc.template;

import com.jn.sqlhelper.common.jdbc.JdbcTemplate;
import com.jn.sqlhelper.common.jdbc.JdbcTemplateFactory;

import javax.sql.DataSource;

public class SpringJdbcTemplateAdapterFactory implements JdbcTemplateFactory {
    @Override
    public JdbcTemplate get(DataSource dataSource) {
        return new SpringJdbcTemplateAdapter(new com.jn.sqlhelper.springjdbc.JdbcTemplate(dataSource));
    }
}
