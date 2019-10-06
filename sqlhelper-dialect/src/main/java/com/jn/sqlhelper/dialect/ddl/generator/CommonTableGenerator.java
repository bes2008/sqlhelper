package com.jn.sqlhelper.dialect.ddl.generator;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.LineDelimiter;
import com.jn.sqlhelper.common.ddlmodel.*;
import com.jn.sqlhelper.common.ddlmodel.internal.BooleanFlag;
import com.jn.sqlhelper.common.ddlmodel.internal.JdbcType;
import com.jn.sqlhelper.common.ddlmodel.internal.SortType;
import com.jn.sqlhelper.common.ddlmodel.internal.TableType;
import com.jn.sqlhelper.common.utils.SQLs;
import com.jn.sqlhelper.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class CommonTableGenerator implements TableGenerator {
    private DatabaseMetaData databaseMetaData;
    private static final Logger logger = LoggerFactory.getLogger(CommonTableGenerator.class);

    public CommonTableGenerator(DatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }

    @Override
    public String generate(Table table) throws SQLException {
        return generateAnyTableDDL(table);
    }

    private String generateAnyTableDDL(Table table) throws SQLException {
        TableType tableType = table.getTableType();
        if (tableType == TableType.SYSTEM_TABLE || tableType == TableType.TABLE || tableType == TableType.GLOBAL_TEMPORARY || tableType == TableType.LOCAL_TEMPORARY) {
            return generateTableDDL(table);
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

    protected String generateTableDDL(final Table table) throws SQLException {
        final String lineDelimiter = LineDelimiter.DEFAULT.getValue();
        final StringBuilder builder = new StringBuilder(256);
        if (Strings.isEmpty(table.getSql())) {
            // create table clause:
            builder.append(generateCreateTableClause(table));

            builder.append("(").append(lineDelimiter);
            // columns
            Utils.forEach(table.getColumns(), new Consumer2<Integer, Column>() {
                @Override
                public void accept(Integer i, Column column) {
                    if (i > 0) {
                        builder.append(",").append(lineDelimiter);
                    }
                    builder.append("\t").append(generateDefineColumnClause(table, column));
                }
            });
            builder.append(lineDelimiter);

            builder.append(");").append(lineDelimiter);

            // primary key
            builder.append(generateAddPrimaryKeyClause(table));

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
                builder.append(generateCreateIndexDDLClause(table, index));
            }
        });
        return builder.toString();
    }

    protected String generateCreateTableClause(final Table table) throws SQLException {
        final StringBuilder builder = new StringBuilder(256);
        builder.append("CREATE");
        TableType tableType = table.getTableType();
        if (tableType == TableType.GLOBAL_TEMPORARY || tableType == TableType.LOCAL_TEMPORARY) {
            builder.append(" ").append(tableType.getCode());
        }
        builder.append(" TABLE ").append(table.getName()).append(LineDelimiter.DEFAULT.getValue());
        return builder.toString();
    }

    protected String generateDefineColumnClause(final Table table, Column column) {
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

        if (jdbcType == JdbcType.REF) {
            ImportedColumn importedColumn = table.getFkColumnMap().get(column.getName());
            if (importedColumn != null) {
                String tableFQN = SQLs.getTableFQN(importedColumn.getPkTableCatalog(), importedColumn.getPkTableSchema(), importedColumn.getPkTableName());
                builder.append(" REFERENCES ").append(tableFQN);
                // referenced columns ???
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

    protected String generateAddPrimaryKeyClause(final Table table) throws SQLException {
        final StringBuilder builder = new StringBuilder(256);
        builder.append("ALTER TABLE ").append(getTableFQN(table.getCatalog(), table.getSchema(), table.getName(), true)).append(" ADD PRIMARY KEY (");
        Utils.forEach(table.getPkColumns(), new Consumer2<Integer, PrimaryKeyColumn>() {
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

    protected String generateCreateIndexDDLClause(Table table, Index index) {
        final StringBuilder builder = new StringBuilder(256);
        builder.append("CREATE INDEX ").append(index.getName()).append(" ON ").append(table).append(" (");
        Utils.forEach(index.getColumns(), new Consumer2<Integer, IndexColumn>() {
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

    protected String getTableFQN(String catalog, String schema, String tableName, boolean catalogSupportedInClause) {
        if (catalogSupportedInClause) {
            String catalogSeparator = null;
            try {
                catalogSeparator = databaseMetaData.getCatalogSeparator();
            } catch (SQLException ex) {

                catalogSeparator = ".";
            }
            return SQLs.getTableFQN(catalog, schema, tableName, catalogSeparator);
        }
        return tableName;
    }
}
