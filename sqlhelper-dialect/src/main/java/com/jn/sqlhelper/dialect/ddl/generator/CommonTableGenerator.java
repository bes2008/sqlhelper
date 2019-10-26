package com.jn.sqlhelper.dialect.ddl.generator;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.ddl.dump.AbstractTableGenerator;
import com.jn.sqlhelper.common.ddl.model.DatabaseDescription;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;

import java.sql.DatabaseMetaData;

public class CommonTableGenerator extends AbstractTableGenerator {
    protected Dialect dialect;

    public CommonTableGenerator(DatabaseDescription databaseDesc) {
        this(databaseDesc, null);
    }

    public CommonTableGenerator(DatabaseDescription databaseDesc, Dialect dialect) {
        super(databaseDesc);
        if (dialect == null) {
            dialect = DialectRegistry.getInstance().getDialectByDatabaseMetadata(databaseDesc.getDbMetaData());
        }
        this.dialect = dialect;
    }

    public CommonTableGenerator(DatabaseMetaData dbMetaData) {
        this(dbMetaData, null);
    }

    public CommonTableGenerator(DatabaseMetaData dbMetaData, Dialect dialect) {
        super(Preconditions.checkNotNull(dbMetaData));
        if (dialect != null) {
            this.dialect = dialect;
        }
        if (this.dialect == null) {
            this.dialect = DialectRegistry.getInstance().getDialectByDatabaseMetadata(databaseDesc.getDbMetaData());
        }
    }
}
