package com.jn.sqlhelper.examples.test.ddlmodel;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.io.IOs;
import com.jn.sqlhelper.common.connection.ConnectionConfiguration;
import com.jn.sqlhelper.common.connection.ConnectionFactory;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
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
        showCatalogs(dbMetaData);
    }

    private void showCatalogs(DatabaseMetaData dbMetaData) throws Exception {
        ResultSet catalogRs = dbMetaData.getCatalogs();
        List<String> catalogs = Collects.emptyArrayList();
        while (catalogRs.next()) {
            String catalog = catalogRs.getString("TABLE_CAT");
            catalogs.add(catalog);
        }

        String catalogSeparator = dbMetaData.getCatalogSeparator();
        String catalogTerm = dbMetaData.getCatalogTerm();
        System.out.println("catalogSeparator: " + catalogSeparator);
        System.out.println("catalogTerm: " + catalogTerm);
        System.out.println("catalogs: " + catalogs.toString());
    }
}
