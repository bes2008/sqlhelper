package com.jn.sqlhelper.dialect.sql.ddl.generator;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.common.ddl.dump.DatabaseLoader;
import com.jn.sqlhelper.common.ddl.dump.TableGenerator;
import com.jn.sqlhelper.common.ddl.model.DatabaseDescription;
import com.jn.sqlhelper.common.ddl.model.Table;
import com.jn.sqlhelper.common.exception.TableNonExistsException;
import com.jn.sqlhelper.common.utils.SQLs;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;

import java.sql.SQLException;

public class DDLs {
    public static String generateTableDDL(@NonNull DatabaseDescription database, String catalog, String schema, @NonNull String tableName) throws SQLException {
        Preconditions.checkNotNull(database);
        Preconditions.checkNotNull(tableName);
        Table table = new DatabaseLoader().loadTable(database, catalog, schema, tableName);
        if (table != null) {
            return createTableGenerator(database).generate(table);
        }
        throw new TableNonExistsException(StringTemplates.formatWithPlaceholder("Table {} is not exists", SQLs.getTableFQN(database, catalog, schema, tableName)));
    }

    private static TableGenerator createTableGenerator(DatabaseDescription databaseDescription) {
        Dialect dialect = DialectRegistry.getInstance().getDialectByDatabaseMetadata(databaseDescription.getDbMetaData());
        return new CommonTableGenerator(databaseDescription, dialect);
    }

}
