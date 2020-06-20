package com.jn.sqlhelper.springjdbc.resultset;

import com.jn.sqlhelper.common.resultset.SelectCountResultSetExtractor;

public class SelectCountRSExtractor extends ResultSetExtractorAdaptor<Integer, SelectCountResultSetExtractor> {
    public SelectCountRSExtractor() {
        this(SelectCountResultSetExtractor.INSTANCE);
    }

    public SelectCountRSExtractor(SelectCountResultSetExtractor delegate) {
        super(delegate);
    }
}
