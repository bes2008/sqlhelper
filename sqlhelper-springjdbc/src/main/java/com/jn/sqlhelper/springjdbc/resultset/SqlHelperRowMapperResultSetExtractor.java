package com.jn.sqlhelper.springjdbc.resultset;

import com.jn.sqlhelper.common.resultset.BeanRowMapper;
import com.jn.sqlhelper.common.resultset.RowMapperResultSetExtractor;

import java.util.List;

public class SqlHelperRowMapperResultSetExtractor<R> extends ResultSetExtractorAdaptor<List<R>, RowMapperResultSetExtractor<R>> {

    public SqlHelperRowMapperResultSetExtractor(RowMapperResultSetExtractor<R> delegate) {
        super(delegate);
    }

    public SqlHelperRowMapperResultSetExtractor(BeanRowMapper<R> beanRowMapper) {
        super(new RowMapperResultSetExtractor<R>(beanRowMapper));
    }
}
