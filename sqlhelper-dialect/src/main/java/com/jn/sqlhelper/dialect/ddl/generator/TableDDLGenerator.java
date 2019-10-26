package com.jn.sqlhelper.dialect.ddl.generator;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.ddl.dump.CommonTableGenerator;
import com.jn.sqlhelper.common.ddl.model.DatabaseDescription;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;

import java.sql.DatabaseMetaData;

public class TableDDLGenerator extends CommonTableGenerator {
    protected Dialect dialect;

    public TableDDLGenerator(DatabaseDescription databaseDesc) {
        this(databaseDesc, null);
    }

    public TableDDLGenerator(DatabaseDescription databaseDesc, Dialect dialect) {
        super(databaseDesc);
        if (dialect == null) {
            dialect = DialectRegistry.getInstance().getDialectByDatabaseMetadata(databaseDesc.getDbMetaData());
        }
        this.dialect = dialect;
    }

    public TableDDLGenerator(DatabaseMetaData dbMetaData) {
        this(dbMetaData, null);
    }

    public TableDDLGenerator(DatabaseMetaData dbMetaData, Dialect dialect) {
        super(Preconditions.checkNotNull(dbMetaData));
        if (dialect != null) {
            this.dialect = dialect;
        }
        if (this.dialect == null) {
            this.dialect = DialectRegistry.getInstance().getDialectByDatabaseMetadata(databaseDesc.getDbMetaData());
        }
    }
}
