package com.jn.sqlhelper.dialect.ddl.generator;

import com.jn.sqlhelper.common.ddlmodel.Table;

import java.sql.SQLException;

public interface TableGenerator {
    String generate(Table table) throws SQLException;
}
