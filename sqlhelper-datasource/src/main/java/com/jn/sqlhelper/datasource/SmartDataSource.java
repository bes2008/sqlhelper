/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.datasource;

import javax.sql.DataSource;
import java.sql.Connection;
/**
 * Extension of the {@code javax.sql.DataSource} interface, to be
 * implemented by special DataSources that return JDBC Connections
 * in an unwrapped fashion.
 *
 * <p>Classes using this interface can query whether or not the Connection
 * should be closed after an operation. Spring's DataSourceUtils and
 * JdbcTemplate classes automatically perform such a check.
 **/
public interface SmartDataSource extends DataSource {

    /**
     * Should we close this Connection, obtained from this DataSource?
     * <p>Code that uses Connections from a SmartDataSource should always
     * perform a check via this method before invoking {@code close()}.
     * <p>Note that the JdbcTemplate class in the 'jdbc.core' package takes care of
     * releasing JDBC Connections, freeing application code of this responsibility.
     * @param con the Connection to check
     * @return whether the given Connection should be closed
     * @see java.sql.Connection#close()
     */
    boolean shouldClose(Connection con);

}

