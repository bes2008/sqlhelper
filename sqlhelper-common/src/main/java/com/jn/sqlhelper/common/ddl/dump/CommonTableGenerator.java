package com.jn.sqlhelper.common.ddl.dump;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.LineDelimiter;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.common.ddl.model.*;
import com.jn.sqlhelper.common.ddl.model.internal.BooleanFlag;
import com.jn.sqlhelper.common.ddl.model.internal.JdbcType;
import com.jn.sqlhelper.common.ddl.model.internal.SortType;
import com.jn.sqlhelper.common.ddl.model.internal.TableType;
import com.jn.sqlhelper.common.utils.SQLs;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class CommonTableGenerator implements TableGenerator {
    protected DatabaseDescription databaseDesc;

    public CommonTableGenerator(DatabaseDescription databaseDesc) {
        this.databaseDesc = databaseDesc;
    }

    public CommonTableGenerator(DatabaseMetaData dbMetaData) {
        this(new DatabaseDescription(dbMetaData));
    }

    @Override
    public String generate(Table table) throws SQLException {
        return generateAnyTableDDL(table);
    }

    protected boolean isSupportsSetPrimaryKeyInTableDDL() {
        return false;
    }

    protected boolean isSupportsSetPrimaryKeyUsingAlter() {
        return !isSupportsSetPrimaryKeyInTableDDL();
    }

    private String generateAnyTableDDL(Table table) throws SQLException {
        TableType tableType = table.getTableType();
        if (tableType == TableType.SYSTEM_TABLE || tableType == TableType.TABLE || tableType == TableType.GLOBAL_TEMPORARY || tableType == TableType.LOCAL_TEMPORARY) {
            return buildTableDDL(table);
        } else {
            final String lineDelimiter = LineDelimiter.DEFAULT.getValue();
            final StringBuilder builder = new StringBuilder(256);

            String sql = table.getSql();
            if (Strings.isNotEmpty(sql)) {
                builder.append(sql);
                if (!sql.endsWith(";")) {
                    builder.append(";");
                }
                builder.append(lineDelimiter);
            } else {
                if (tableType == TableType.ALIAS) {
                    builder.append("CREATE ALIAS ");
                } else if (tableType == TableType.SYNONYM) {
                    builder.append("CREATE SYNONYM ");
                } else {
                    builder.append("CREATE VIEW ");
                }
                builder.append(table.getName()).append(";").append(lineDelimiter);
            }
            return builder.toString();
        }
    }

    protected String buildTableDDL(final Table table) throws SQLException {
        final String lineDelimiter = LineDelimiter.DEFAULT.getValue();
        final StringBuilder builder = new StringBuilder(256);
        if (Strings.isEmpty(table.getSql())) {
            // create table clause:
            builder.append(buildCreateTableClause(table));

            builder.append("(").append(lineDelimiter);
            // columns
            Collects.forEach(table.getColumns(), new Consumer2<Integer, Column>() {
                @Override
                public void accept(Integer i, Column column) {
                    if (i > 0) {
                        builder.append(",").append(lineDelimiter);
                    }
                    builder.append("\t").append(buildDefineColumnClause(table, column));
                }
            });
            builder.append(lineDelimiter);

            Holder<Boolean> hasSetPrimaryKeys = new Holder<Boolean>(false);
            Holder<Boolean> hasSetReferenceKeys = new Holder<Boolean>(false);
            String afterAllColumnString = buildClausesAfterAllColumns(table, hasSetPrimaryKeys, hasSetReferenceKeys);
            if (Strings.isNotEmpty(afterAllColumnString)) {
                builder.append(",").append(afterAllColumnString);
            }

            builder.append(");").append(lineDelimiter);

            // primary key
            if (table.hasPrimaryKeys() && !hasSetPrimaryKeys.get() && isSupportsSetPrimaryKeyUsingAlter()) {
                builder.append(buildAlterAddPrimaryKeyClause(table));
            }

        } else {
            String sql = table.getSql();
            builder.append(sql);
            if (!sql.endsWith(";")) {
                builder.append(";");
            }
            builder.append(lineDelimiter);
        }

        // indexes
        Collects.forEach(table.getIndexMap(), new Consumer2<String, Index>() {
            @Override
            public void accept(String key, Index index) {
                builder.append(buildCreateIndexDDLClause(table, index));
            }
        });
        return builder.toString();
    }

    protected String buildCreateTableClause(final Table table) throws SQLException {
        final StringBuilder builder = new StringBuilder(256);
        builder.append("CREATE");
        TableType tableType = table.getTableType();
        if (tableType == TableType.GLOBAL_TEMPORARY || tableType == TableType.LOCAL_TEMPORARY) {
            builder.append(" ").append(tableType.getCode());
        }
        String tableFQN = getTableFQN(databaseDesc.supportsCatalogsInTableDefinitions() ? table.getCatalog() : null, databaseDesc.supportsSchemasInTableDefinitions() ? table.getSchema() : null, table.getName());
        builder.append(" TABLE ").append(tableFQN).append(LineDelimiter.DEFAULT.getValue());
        return builder.toString();
    }

    protected String buildDefineColumnClause(final Table table, Column column) {
        StringBuilder builder = new StringBuilder(256);
        builder.append(column.getName()).append(" ").append(column.getTypeName());

        // size
        JdbcType jdbcType = column.getJdbcType();
        if (jdbcType == JdbcType.VARCHAR || jdbcType == JdbcType.LONGVARCHAR || jdbcType == JdbcType.NVARCHAR || jdbcType == JdbcType.LONGNVARCHAR) {
            builder.append("(").append(column.getCharOctetLength()).append(")");
        } else {
            if (jdbcType == JdbcType.CHAR) {
                builder.append("(").append(column.getSize()).append(")");
            }
            // others types
            // ...
        }

        if (column.getIsNullable() == BooleanFlag.NO) {
            builder.append(" NOT NULL");
        }

        if (column.getDefaultValue() != null) {
            builder.append(" DEFAULT '").append(column.getDefaultValue()).append("'");
        }

        if (column.getIsAutoincrement() == BooleanFlag.YES) {
            builder.append(" AUTO_INCREMENT");
        }

        if (Strings.isNotEmpty(column.getRemarks())) {
            builder.append(" COMMENT '").append(column.getRemarks()).append("'");
        }

        // foreign key references
        if (jdbcType == JdbcType.REF) {
            ImportedColumn importedColumn = table.getFkColumnMap().get(column.getName());
            if (importedColumn != null) {
                String tableFQN = getTableFQN(databaseDesc.supportsCatalogsInTableDefinitions() ? importedColumn.getPkTableCatalog() : null, databaseDesc.supportsSchemasInTableDefinitions() ? importedColumn.getPkTableSchema() : null, importedColumn.getPkTableName());
                builder.append(" REFERENCES ").append(tableFQN);
                builder.append(" (");
                builder.append(importedColumn.getPkColumnName());
                builder.append(")");

                if (importedColumn.getDeleteRule() != null) {
                    builder.append(" ON DELETE ").append(importedColumn.getDeleteRule().getKeywords());
                }
                if (importedColumn.getUpdateRule() != null) {
                    builder.append(" ON UPDATE ").append(importedColumn.getUpdateRule().getKeywords());
                }
            }
        }
        return builder.toString();
    }

    protected String buildClausesAfterAllColumns(Table table, Holder<Boolean> hasSetPrimaryKeys, Holder<Boolean> hasSetReferenceKeys) {
        return "";
    }

    protected String buildAlterAddPrimaryKeyClause(final Table table) throws SQLException {
        final StringBuilder builder = new StringBuilder(256);
        String tableFQN = getTableFQN(databaseDesc.supportsCatalogsInTableDefinitions() ? table.getCatalog() : null, databaseDesc.supportsSchemasInTableDefinitions() ? table.getSchema() : null, table.getName());
        builder.append("ALTER TABLE ").append(tableFQN).append(" ADD PRIMARY KEY (");
        Collects.forEach(table.getPkColumns(), new Consumer2<Integer, PrimaryKeyColumn>() {
            @Override
            public void accept(Integer i, PrimaryKeyColumn pkColumn) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(pkColumn.getColumnName());
            }
        });
        builder.append(");").append(LineDelimiter.DEFAULT.getValue());
        return builder.toString();
    }

    protected String buildCreateIndexDDLClause(Table table, Index index) {
        final StringBuilder builder = new StringBuilder(256);
        String tableFQN = getTableFQN(databaseDesc.supportsCatalogsInIndexDefinitions() ? table.getCatalog() : null, databaseDesc.supportsSchemasInIndexDefinitions() ? table.getSchema() : null, table.getName());
        builder.append("CREATE INDEX ").append(index.getName()).append(" ON ").append(tableFQN).append(" (");
        Collects.forEach(index.getColumns(), new Consumer2<Integer, IndexColumn>() {
            @Override
            public void accept(Integer i, IndexColumn indexColumn) {
                if (i > 0) {
                    builder.append(", ");
                }

                builder.append(indexColumn.getColumnName());
                if (indexColumn.getAscOrDesc() != SortType.UNSUPPORTED) {
                    builder.append(" ").append(indexColumn.getAscOrDesc().name());
                }
            }
        });
        builder.append(");").append(LineDelimiter.DEFAULT.getValue());
        return builder.toString();
    }

    /**
     * @param catalog   null if catalog is not supported
     * @param schema    null if schema is not supported
     * @param tableName the table name
     * @return table full qualified name
     */
    protected String getTableFQN(String catalog, String schema, String tableName) {
        String catalogSeparator = databaseDesc.getCatalogSeparator();
        return SQLs.getTableFQN(catalog, schema, tableName, catalogSeparator, databaseDesc.isCatalogAtStart());
    }


}
