package com.jn.sqlhelper.apachedbutils.resultset;

import com.jn.sqlhelper.common.resultset.UpdatedRowsResultSetExtractor;

public class UpdatedRowsResultSetHandler extends ResultSetHandlerExtractorAdapter<Integer, UpdatedRowsResultSetExtractor> {

    public UpdatedRowsResultSetHandler() {
        super(UpdatedRowsResultSetExtractor.INSTANCE);
    }

}
