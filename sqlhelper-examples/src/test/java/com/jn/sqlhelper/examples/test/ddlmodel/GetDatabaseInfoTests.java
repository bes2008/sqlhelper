package com.jn.sqlhelper.examples.test.ddlmodel;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.IOs;
import com.jn.sqlhelper.common.connection.ConnectionConfiguration;
import com.jn.sqlhelper.common.connection.ConnectionFactory;
import com.jn.sqlhelper.common.ddlmodel.Column;
import com.jn.sqlhelper.common.ddlmodel.Table;
import com.jn.sqlhelper.common.resultset.BeanRowMapper;
import com.jn.sqlhelper.common.resultset.RowMapperResultSetExtractor;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GetDatabaseInfoTests {
    @Test
    public void test() throws Throwable {
        InputStream inputStream = GetDatabaseInfoTests.class.getClassLoader().getResourceAsStream("jdbc.properties");
        ConnectionConfiguration connectionConfiguration = null;
        try {
            connectionConfiguration = ConnectionConfiguration.loadConfig(inputStream);
        } finally {
            IOs.close(inputStream);
        }

        ConnectionFactory connectionFactory = new ConnectionFactory(connectionConfiguration);
        Connection connection = connectionFactory.getConnection();

        DatabaseMetaData dbMetaData = connection.getMetaData();
        // showCatalogs(dbMetaData);
        // showSchemas(dbMetaData);
        showTables(dbMetaData);
    }

    /**
     * catalog.schema.table
     *
     * @param dbMetaData
     * @throws SQLException
     */
    private void showCatalogs(DatabaseMetaData dbMetaData) throws SQLException {
        ResultSet catalogRs = dbMetaData.getCatalogs();
        List<String> catalogs = Collects.emptyArrayList();
        while (catalogRs.next()) {
            String catalog = catalogRs.getString("TABLE_CAT");
            catalogs.add(catalog);
        }

        String catalogSeparator = dbMetaData.getCatalogSeparator();
        String catalogTerm = dbMetaData.getCatalogTerm();
        boolean catalogAtStart = dbMetaData.isCatalogAtStart();
        int maxCatalogLength = dbMetaData.getMaxCatalogNameLength();

        System.out.println("maxCatalogNameLength: " + maxCatalogLength);
        System.out.println("catalogSeparator: " + catalogSeparator); // .
        System.out.println("catalogTerm: " + catalogTerm); //catalog
        System.out.println("catalogs: " + catalogs.toString());
        System.out.println("catalog at start ? :" + catalogAtStart);
    }

    private void showSchemas(DatabaseMetaData dbMetaData) throws SQLException {

        ResultSet schemasRs = dbMetaData.getSchemas();
        while (schemasRs.next()) {
            String schema = schemasRs.getString("TABLE_SCHEM");
            String catalog = schemasRs.getString("TABLE_CATALOG");
            System.out.println(catalog + " " + schema);
        }

        String schemaTerm = dbMetaData.getSchemaTerm();
        System.out.println("schema term: " + schemaTerm);

        int maxSchemaNameLength = dbMetaData.getMaxSchemaNameLength();
        System.out.println("maxSchemaNameLength: " + maxSchemaNameLength);
    }

    private void showTables(DatabaseMetaData dbMetaData) throws SQLException {
        int maxTableNameLength = dbMetaData.getMaxTableNameLength();
        System.out.println("maxTableNameLength: " + maxTableNameLength);

        ResultSet tablesRs = dbMetaData.getTables("TEST", "PUBLIC", null, null);
        List<Table> tables = new RowMapperResultSetExtractor<Table>(new BeanRowMapper<>(Table.class)).extract(tablesRs);

        for (Table table : tables) {
            System.out.println(table);
            @Nullable
            String catalog = table.getCatalog();
            @Nullable
            String schema = table.getSchema();
            @NonNull
            String tableName = table.getName();
            System.out.println("Columns:");
            showColumn(dbMetaData, catalog, schema, tableName);
        }

    }

    private void showColumn(DatabaseMetaData dbMetaData, String catalog, String schema, String tableName) throws SQLException {
        ResultSet columnsRs = dbMetaData.getColumns(catalog, schema, tableName, null);
        List<Column> columns = new RowMapperResultSetExtractor<Column>(new BeanRowMapper<Column>(Column.class)).extract(columnsRs);
        Collects.forEach(columns, new Consumer<Column>() {
            @Override
            public void accept(Column column) {
                System.out.println(column);
            }
        });

    }
}
