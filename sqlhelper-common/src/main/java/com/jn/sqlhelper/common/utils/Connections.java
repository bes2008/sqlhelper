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

package com.jn.sqlhelper.common.utils;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.reflect.Reflects;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class Connections {

    public static String getCatalog(Connection connection) {
        Preconditions.checkNotNull(connection);
        try {
            return connection.getCatalog();
        } catch (SQLException ex) {
            return null;
        }
    }


    private static final Method CONNECTION_GET_SCHEMA = Reflects.getDeclaredMethod(Connection.class, "getSchema");

    public static String getSchema(Connection connection) {
        Preconditions.checkNotNull(connection);
        if (CONNECTION_GET_SCHEMA != null) {
            try {
                return Reflects.<String>invoke(CONNECTION_GET_SCHEMA, connection, null, true, true);
            } catch (Throwable ex) {
                return null;
            }
        }
        return null;
    }


    public static String getDatabaseProductName(DataSource dataSource) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            return metaData.getDatabaseProductName();
        } finally {
            IOs.close(conn);
        }
    }
}
