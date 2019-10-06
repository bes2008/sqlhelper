package com.jn.sqlhelper.dialect.ddl.generator;

import com.jn.sqlhelper.common.ddlmodel.Table;

import java.sql.DatabaseMetaData;

public interface TableGenerator {
    String generate(Table table, DatabaseMetaData databaseMetaData);
}
