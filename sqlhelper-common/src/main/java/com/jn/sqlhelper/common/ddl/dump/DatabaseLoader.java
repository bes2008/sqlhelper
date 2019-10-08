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

import com.jn.sqlhelper.common.ddl.model.DatabaseDescription;
import com.jn.sqlhelper.common.ddl.model.Table;
import com.jn.sqlhelper.common.ddl.model.internal.TableType;
import com.jn.sqlhelper.common.resultset.BeanRowMapper;
import com.jn.sqlhelper.common.resultset.RowMapperResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DatabaseLoader {

    private static final String[] tableTypes = new String[]{
            TableType.GLOBAL_TEMPORARY.getCode(),
            TableType.LOCAL_TEMPORARY.getCode(),
            TableType.TABLE.getCode()
    };

    public List<Table> loadTables(DatabaseDescription databaseDescription, String catalogNamePattern, String schemaNamePattern, String tableNamePattern) throws SQLException {
        ResultSet tablesRs = databaseDescription.getDbMetaData().getTables(catalogNamePattern, schemaNamePattern, tableNamePattern, tableTypes);
        return new RowMapperResultSetExtractor<Table>(new BeanRowMapper<Table>(Table.class)).extract(tablesRs);
    }

    public Table loadTable(DatabaseDescription databaseDescription, String catalog, String schema, String tableName) throws SQLException {
        List<Table> tables = loadTables(databaseDescription, catalog, schema, tableName);
        if (!tables.isEmpty()) {
            return tables.get(0);
        }
        return null;
    }
}
