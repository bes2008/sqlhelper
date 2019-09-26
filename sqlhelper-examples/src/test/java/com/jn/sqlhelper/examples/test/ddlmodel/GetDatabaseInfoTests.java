package com.jn.sqlhelper.examples.test.ddlmodel;

import com.jn.langx.util.io.IOs;
import com.jn.sqlhelper.common.connection.ConnectionConfiguration;
import com.jn.sqlhelper.common.connection.ConnectionFactory;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

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
        ResultSet catalogs = dbMetaData.getCatalogs();
        String catalogSeparator = dbMetaData.getCatalogSeparator();
        String catalogTerm = dbMetaData.getCatalogTerm();
        System.out.println("catalogSeparator: " + catalogSeparator);
        System.out.println("catalogTerm: " + catalogTerm);
    }
}
