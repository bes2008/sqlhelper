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

package com.jn.sqlhelper.common.transaction;

import com.jn.langx.Delegatable;
import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

import java.sql.Connection;

public enum Isolation implements CommonEnum, Delegatable<EnumDelegate> {
    /**
     * Read Committed Isolation level. This is typically the default for most
     * configurations.
     */
    READ_COMMITED(Connection.TRANSACTION_READ_COMMITTED, "READ_COMMITTED", "TRANSACTION_READ_COMMITTED"),

    /**
     * Read uncommitted Isolation level.
     */
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED, "READ_UNCOMMITTED", "TRANSACTION_READ_UNCOMMITTED"),

    /**
     * Repeatable Read Isolation level.
     */
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ, "REPEATABLE_READ", "TRANSACTION_REPEATABLE_READ"),

    /**
     * Serializable Isolation level.
     */
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE, "SERIALIZABLE", "TRANSACTION_SERIALIZABLE"),

    /**
     * No Isolation level.
     */
    NONE(Connection.TRANSACTION_NONE, "NONE", "TRANSACTION_NONE"),

    /**
     * The default isolation level. This typically means the default that the
     * DataSource is using or configured to use.
     */
    DEFAULT(-1, "DEFAULT", "DEFAULT");
    private EnumDelegate delegate;

    private Isolation(int code, String name, String description) {
        this.delegate = new EnumDelegate(code, name, description);
    }

    @Override
    public int getCode() {
        return delegate.getCode();
    }

    @Override
    public String getDisplayText() {
        return delegate.getDisplayText();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }


    @Override
    public EnumDelegate getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(EnumDelegate enumDelegate) {

    }
}
