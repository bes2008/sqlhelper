/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.datasource.NamedDataSource;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class DynamicDataSourceManagedTransaction implements Transaction {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceManagedTransaction.class);
    private final DataSource dataSource;

    private Connection connection;
    private boolean autoCommit = false;


    public DynamicDataSourceManagedTransaction(DataSource dataSource) {
        Preconditions.checkNotNull(dataSource, "No DataSource specified");
        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (this.connection == null) {
            openConnection();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("get a connection {} from {}", connection, ((NamedDataSource) dataSource).getDataSourceKey().getId());
        }
        return this.connection;
    }

    /**
     * Gets a connection from Spring transaction manager and discovers if this
     * {@code Transaction} should manage connection or let it to Spring.
     * <p>
     * It also reads autocommit setting because when using Spring Transaction MyBatis
     * thinks that autocommit is always false and will always call commit/rollback
     * so we need to no-op that calls.
     */
    private void openConnection() throws SQLException {
        this.connection = this.dataSource.getConnection();
        this.autoCommit = this.connection.getAutoCommit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.isClosed() && !autoCommit) {
            connection.commit();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.isClosed() && !autoCommit) {
            connection.rollback();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            if (logger.isDebugEnabled()) {
                logger.debug("close a connection {} from {}", connection, ((NamedDataSource) dataSource).getDataSourceKey().getId());
            }
            connection.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }

}
