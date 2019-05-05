package com.fjn.helper.sql.dialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PrepareParameterSetter<P extends QueryParameters> {
    int setParameters(final PreparedStatement statement, final P queryParameters, final int startIndex) throws SQLException;
}
