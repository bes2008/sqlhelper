package com.jn.sqlhelper.datasource.transaction;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.multivalue.CommonMultiValueMap;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.function.Supplier;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 单JVM多数据源事务
 */
public class LocalizeGlobalTransaction implements Transaction {
    private MultiValueMap<DataSourceKey, Connection> connectionMap = new CommonMultiValueMap<DataSourceKey, Connection>(new LinkedHashMap<DataSourceKey, Collection<Connection>>(), new Supplier<DataSourceKey, Collection<Connection>>() {
        @Override
        public Collection<Connection> get(DataSourceKey key) {
            return Collects.emptyArrayList();
        }
    });

    private boolean rollbackOnly = false;

    public void add(DataSourceKey key, Connection connection) {
        this.connectionMap.add(key, connection);
    }

    @Override
    public void commit() throws SQLException {
        if (rollbackOnly) {
            rollback();
        }
        for (Map.Entry<DataSourceKey, Collection<Connection>> entry : connectionMap.entrySet()) {
            Collection<Connection> connections = entry.getValue();
            for (Connection connection : connections) {
                if (!connection.isClosed()) {
                    connection.commit();
                }
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        for (Map.Entry<DataSourceKey, Collection<Connection>> entry : connectionMap.entrySet()) {
            Collection<Connection> connections = entry.getValue();
            for (Connection connection : connections) {
                if (!connection.isClosed()) {
                    connection.rollback();
                }
            }
        }
    }

    @Override
    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    @Override
    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }
}
