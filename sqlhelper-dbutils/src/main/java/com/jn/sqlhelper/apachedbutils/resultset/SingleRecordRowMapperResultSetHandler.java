package com.jn.sqlhelper.apachedbutils.resultset;

import com.jn.sqlhelper.common.resultset.RowMapper;
import com.jn.sqlhelper.common.resultset.SingleRecordRowMapperResultSetExtractor;

public class SingleRecordRowMapperResultSetHandler<BEAN> extends ResultSetHandlerExtractorAdapter<BEAN, SingleRecordRowMapperResultSetExtractor<BEAN>> {
    public SingleRecordRowMapperResultSetHandler() {
        super();
    }

    public SingleRecordRowMapperResultSetHandler(SingleRecordRowMapperResultSetExtractor<BEAN> resultSetExtractor) {
        super(resultSetExtractor);
    }

    public SingleRecordRowMapperResultSetHandler(RowMapper<BEAN> rowMapper){
        this(new SingleRecordRowMapperResultSetExtractor<BEAN>(rowMapper));
    }

}
