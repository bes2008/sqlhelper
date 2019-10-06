package com.jn.sqlhelper.dialect.ddl.generator;

import com.jn.sqlhelper.common.ddlmodel.Table;

public interface TableGenerator {
    String generate(Table table);
}
