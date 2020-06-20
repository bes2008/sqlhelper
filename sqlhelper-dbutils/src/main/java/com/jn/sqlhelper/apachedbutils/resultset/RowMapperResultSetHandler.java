package com.jn.sqlhelper.apachedbutils.resultset;

import com.jn.sqlhelper.common.resultset.RowMapper;
import com.jn.sqlhelper.common.resultset.RowMapperResultSetExtractor;

import java.util.List;

public class RowMapperResultSetHandler<BEAN> extends ResultSetHandlerExtractorAdapter<List<BEAN>, RowMapperResultSetExtractor<BEAN>> {
    public RowMapperResultSetHandler() {
        super();
    }

    public RowMapperResultSetHandler(RowMapperResultSetExtractor<BEAN> resultSetExtractor) {
        super(resultSetExtractor);
    }

    public RowMapperResultSetHandler(RowMapper<BEAN> rowMapper){
        this(new RowMapperResultSetExtractor<BEAN>(rowMapper));
    }
}
