package com.jn.sqlhelper.common.ddl.dump;

import com.jn.sqlhelper.common.ddl.model.Table;

import java.sql.SQLException;

public interface TableGenerator {
    String generate(Table table) throws SQLException;
}
