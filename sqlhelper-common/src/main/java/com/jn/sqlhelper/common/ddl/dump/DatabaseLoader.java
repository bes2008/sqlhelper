/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.common.ddl.dump;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.sqlhelper.common.ddl.model.*;
import com.jn.sqlhelper.common.ddl.model.internal.TableType;
import com.jn.sqlhelper.common.resultset.BeanRowMapper;
import com.jn.sqlhelper.common.resultset.RowMapperResultSetExtractor;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DatabaseLoader {

    private static final String[] tableTypes = new String[]{
            TableType.GLOBAL_TEMPORARY.getCode(),
            TableType.LOCAL_TEMPORARY.getCode(),
            TableType.TABLE.getCode()
    };

    public Table loadTable(DatabaseDescription databaseDescription, String catalog, String schema, String tableName) throws SQLException {
        List<Table> tables = loadTables(databaseDescription, catalog, schema, tableName);
        if (!tables.isEmpty()) {
            return tables.get(0);
        }
        return null;
    }

    public List<Table> loadTables(DatabaseDescription databaseDescription, String catalogNamePattern, String schemaNamePattern, String tableNamePattern) throws SQLException {
        ResultSet tablesRs = databaseDescription.getDbMetaData().getTables(catalogNamePattern, schemaNamePattern, tableNamePattern, tableTypes);
        List<Table> tables = new RowMapperResultSetExtractor<Table>(new BeanRowMapper<Table>(Table.class)).extract(tablesRs);
        DatabaseMetaData dbMetaData = databaseDescription.getDbMetaData();
        for (Table table : tables) {
            findColumns(dbMetaData, table);

            findTablePKs(dbMetaData, table);

            findTableIndexes(dbMetaData, table);

            findTableFKs(dbMetaData, table);
        }
        return tables;
    }

    private void findColumns(DatabaseMetaData dbMetaData, final Table table) throws SQLException {
        ResultSet columnsRs = dbMetaData.getColumns(table.getCatalog(), table.getSchema(), table.getName(), null);
        List<Column> columns = new RowMapperResultSetExtractor<Column>(new BeanRowMapper<Column>(Column.class)).extract(columnsRs);
        Collects.forEach(columns, new Consumer<Column>() {
            @Override
            public void accept(Column column) {
                table.addColumn(column);
            }
        });
    }

    private void findTableIndexes(DatabaseMetaData dbMetaData, final Table table) throws SQLException {
        ResultSet indexesRs = dbMetaData.getIndexInfo(table.getCatalog(), table.getSchema(), table.getName(), false, false);

        List<IndexColumn> indexes = new RowMapperResultSetExtractor<IndexColumn>(new BeanRowMapper<IndexColumn>(IndexColumn.class)).extract(indexesRs);
        Collects.forEach(indexes, new Consumer<IndexColumn>() {
            @Override
            public void accept(IndexColumn indexColumn) {
                String indexName = indexColumn.getIndexName();
                Index index = table.getIndex(indexName);
                if (index == null) {
                    index = new Index(table.getCatalog(), table.getSchema(), table.getName(), indexName);
                    table.addIndex(index);
                }

                index.addColumn(indexColumn);
            }
        });
    }

    private void findTablePKs(DatabaseMetaData dbMetaData, Table table) throws SQLException {
        ResultSet pkRs = dbMetaData.getPrimaryKeys(table.getCatalog(), table.getSchema(), table.getName());
        List<PrimaryKeyColumn> pkColumns = new RowMapperResultSetExtractor<PrimaryKeyColumn>(new BeanRowMapper<PrimaryKeyColumn>(PrimaryKeyColumn.class)).extract(pkRs);
        for (PrimaryKeyColumn pk : pkColumns) {
            table.addPKColumn(pk);
        }
    }

    private void findTableFKs(DatabaseMetaData dbMetaData, Table table) throws SQLException {
        ResultSet fkRs = dbMetaData.getImportedKeys(table.getCatalog(), table.getSchema(), table.getName());
        List<ImportedColumn> fkColumns = new RowMapperResultSetExtractor<ImportedColumn>(new BeanRowMapper<ImportedColumn>(ImportedColumn.class)).extract(fkRs);
        for (ImportedColumn fk : fkColumns) {
            table.addFKColumn(fk);
        }
    }


}
