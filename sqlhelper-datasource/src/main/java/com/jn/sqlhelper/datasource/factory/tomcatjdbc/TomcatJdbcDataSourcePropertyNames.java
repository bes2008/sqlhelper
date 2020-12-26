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

package com.jn.sqlhelper.datasource.factory.tomcatjdbc;

/**
 * ref: http://tomcat.apache.org/tomcat-8.5-doc/jdbc-pool.html
 */
public class TomcatJdbcDataSourcePropertyNames {
    /**
     * <pre>
     *     (boolean) The default auto-commit state of connections created by this pool.
     *     If not set, default is JDBC driver default (If not set then the setAutoCommit method will not be called.)
     * </pre>
     */
    public static final String PROP_AUTO_COMMIT = "defaultAutoCommit";
    public static final String PROP_READONLY = "defaultReadOnly";

    /**
     * <pre>
     * (String) The default TransactionIsolation state of connections created by this pool. One of the following: (see javadoc )
     *
     * NONE
     * READ_COMMITTED
     * READ_UNCOMMITTED
     * REPEATABLE_READ
     * SERIALIZABLE
     * If not set, the method will not be called and it defaults to the JDBC driver.
     * </pre>
     */
    public static final String PROP_TRANSACTION_ISOLATION = "defaultTransactionIsolation";
    public static final String PROP_CATALOG = "defaultCatalog";
    public static final String PROP_DRIVER_CLASSNAME = "driverClassName";

    public static final String PROP_PASSWORD = "password";
    public static final String PROP_URL = "url";
    public static final String PROP_USERNAME = "username";

    public static final String PROP_MAX_ACTIVE = "maxActive";
    public static final String PROP_MAX_IDLE = "maxIdle";
    public static final String PROP_MIN_IDLE = "minIdle";
    public static final String PROP_INITIAL_SIZE = "initialSize";
    public static final String PROP_VALIDATION_QUERY = "validationQuery";


}
