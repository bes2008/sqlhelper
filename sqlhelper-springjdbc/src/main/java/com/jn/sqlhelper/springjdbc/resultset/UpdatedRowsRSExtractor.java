package com.jn.sqlhelper.springjdbc.resultset;

import com.jn.sqlhelper.common.resultset.UpdatedRowsResultSetExtractor;

public class UpdatedRowsRSExtractor extends ResultSetExtractorAdaptor<Integer, UpdatedRowsResultSetExtractor> {
    public UpdatedRowsRSExtractor() {
        this(UpdatedRowsResultSetExtractor.INSTANCE);
    }

    public UpdatedRowsRSExtractor(UpdatedRowsResultSetExtractor delegate) {
        super(delegate);
    }
}
