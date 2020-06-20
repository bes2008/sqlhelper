package com.jn.sqlhelper.apachedbutils.resultset;

import com.jn.sqlhelper.common.resultset.SelectCountResultSetExtractor;

public class SelectCountResultSetHandler extends ResultSetHandlerExtractorAdapter<Integer,SelectCountResultSetExtractor> {

    public SelectCountResultSetHandler() {
        super(SelectCountResultSetExtractor.INSTANCE);
    }
}
