/*
 * Copyright 2020 the original author or authors.
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

package com.jn.sqlhelper.datasource.config;

import com.jn.langx.configuration.Configuration;
import com.jn.langx.util.Strings;
import com.jn.sqlhelper.datasource.DataSources;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

import java.util.Properties;

/**
 * 配置单个数据源
 * @since 3.4.0
 */
public class DataSourceProperties implements Configuration {
    /**
     * datasource properties:
     *
     * @see DataSourceGroupProperties#setName(String)
     */
    private String group = DataSources.DATASOURCE_PRIMARY_GROUP;
    private String name;
    private String implementation;
    private boolean primary = false;

    /**
     * common driver properties:
     */
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    private String catalog;
    private String schema;

    private boolean isReadOnly;
    private boolean isAutoCommit;
    /**
     * 可选值:
     * <pre>
     *      NONE,
     *      READ_COMMITTED,
     *      READ_UNCOMMITTED,
     *      REPEATABLE_READ,
     *      SERIALIZABLE
     * </pre>
     */
    private String transactionIsolation;

    private long leakDetectionThresholdInMills;
    private String validationQuery = "select 1";

    private long connectionTimeoutInMills;
    private long validationTimeoutInMills;

    private long idleTimeoutInMills;
    private long maxLifetimeInMills;
    private int maxPoolSize;
    private int initialSize = 0;
    private int minIdle;

    /**
     * custom driver properties:
     */
    private Properties driverProps;

    public DataSourceProperties() {
        this.leakDetectionThresholdInMills = 0L;
        this.validationQuery = null;
        this.connectionTimeoutInMills = 30000L;
        this.validationTimeoutInMills = 5000L;
        this.idleTimeoutInMills = 1800000L;
        this.maxLifetimeInMills = 1800000L;
        this.maxPoolSize = 10;
        this.minIdle = 10;
        this.isAutoCommit = true;
        this.isReadOnly = false;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(final String catalog) {
        this.catalog = Strings.getNullIfEmpty(catalog);
    }

    public long getLeakDetectionThresholdInMills() {
        return this.leakDetectionThresholdInMills;
    }

    public void setLeakDetectionThresholdInMills(final long leakDetectionThresholdInMills) {
        this.leakDetectionThresholdInMills = leakDetectionThresholdInMills;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public long getConnectionTimeoutInMills() {
        return this.connectionTimeoutInMills;
    }

    public void setConnectionTimeoutInMills(final long connectionTimeoutInMills) {
        this.connectionTimeoutInMills = connectionTimeoutInMills;
    }

    public long getValidationTimeoutInMills() {
        return this.validationTimeoutInMills;
    }

    public void setValidationTimeoutInMills(final long validationTimeoutInMills) {
        this.validationTimeoutInMills = validationTimeoutInMills;
    }

    public long getIdleTimeoutInMills() {
        return this.idleTimeoutInMills;
    }

    public void setIdleTimeoutInMills(final long idleTimeoutInMills) {
        this.idleTimeoutInMills = idleTimeoutInMills;
    }

    public long getMaxLifetimeInMills() {
        return this.maxLifetimeInMills;
    }

    public void setMaxLifetimeInMills(final long maxLifetimeInMills) {
        this.maxLifetimeInMills = maxLifetimeInMills;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public void setMaxPoolSize(final int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMinIdle() {
        return this.minIdle;
    }

    public void setMinIdle(final int minIdle) {
        this.minIdle = minIdle;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setDriverClassName(final String driverClassName) {
        this.driverClassName = Strings.getNullIfEmpty(driverClassName);
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = Strings.getNullIfEmpty(url);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = Strings.getNullIfEmpty(username);
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = Strings.getNullIfEmpty(password);
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = Strings.getNullIfEmpty(name);
    }

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(final String schema) {
        this.schema = Strings.getNullIfEmpty(schema);
    }

    public boolean isAutoCommit() {
        return this.isAutoCommit;
    }

    public void setAutoCommit(final boolean autoCommit) {
        this.isAutoCommit = autoCommit;
    }

    public String getValidationQuery() {
        return this.validationQuery;
    }

    public void setValidationQuery(final String validationQuery) {
        this.validationQuery = Strings.getNullIfEmpty(validationQuery);
    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    public void setReadOnly(final boolean readOnly) {
        this.isReadOnly = readOnly;
    }

    public String getTransactionIsolation() {
        return this.transactionIsolation;
    }

    public void setTransactionIsolation(final String transactionIsolation) {
        this.transactionIsolation = Strings.getNullIfEmpty(transactionIsolation);
    }

    public Properties getDriverProps() {
        return driverProps;
    }

    public void setDriverProps(Properties driverProps) {
        this.driverProps = driverProps;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    @Override
    public String getId() {
        return group + DataSources.getDatasourceIdSeparator() + name;
    }

    @Override
    public void setId(String idString) {
        DataSourceKey dataSourceKey = DataSources.buildDataSourceKey(idString);
        setGroup(dataSourceKey.getGroup());
        setName(dataSourceKey.getName());
    }

    public DataSourceKey getDataSourceKey() {
        return new DataSourceKey(group, name);
    }


    @Override
    public String toString() {
        return "DataSourceProperties{" +
                "group='" + group + '\'' +
                ", name='" + name + '\'' +
                ", implementation='" + implementation + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", catalog='" + catalog + '\'' +
                ", schema='" + schema + '\'' +
                ", isReadOnly=" + isReadOnly +
                ", isAutoCommit=" + isAutoCommit +
                ", transactionIsolation='" + transactionIsolation + '\'' +
                ", leakDetectionThresholdInMills=" + leakDetectionThresholdInMills +
                ", validationQuery='" + validationQuery + '\'' +
                ", connectionTimeoutInMills=" + connectionTimeoutInMills +
                ", validationTimeoutInMills=" + validationTimeoutInMills +
                ", idleTimeoutInMills=" + idleTimeoutInMills +
                ", maxLifetimeInMills=" + maxLifetimeInMills +
                ", maxPoolSize=" + maxPoolSize +
                ", initialSize=" + initialSize +
                ", minIdle=" + minIdle +
                ", driverProps=" + driverProps +
                '}';
    }
}
