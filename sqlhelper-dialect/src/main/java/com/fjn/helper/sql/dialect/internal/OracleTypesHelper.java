/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 2.1 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.SQLDialectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OracleTypesHelper {
    private static final Logger log = LoggerFactory.getLogger(OracleTypesHelper.class);

    public static final OracleTypesHelper INSTANCE = new OracleTypesHelper();

    private static final String ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.OracleTypes";
    private static final String DEPRECATED_ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.driver.OracleTypes";
    private final int oracleCursorTypeSqlType;

    private OracleTypesHelper() {
        int typeCode = -99;
        try {
            typeCode = extractOracleCursorTypeValue();
        } catch (Exception e) {
            log.warn("Unable to resolve Oracle CURSOR JDBC type code", e);
        }
        this.oracleCursorTypeSqlType = typeCode;
    }

    private int extractOracleCursorTypeValue() {
        try {
            return locateOracleTypesClass().getField("CURSOR").getInt(null);
        } catch (Exception se) {
            throw new SQLDialectException("Unable to access OracleTypes.CURSOR value", se);
        }
    }

    private Class locateOracleTypesClass() {
        try {
            return Class.forName("oracle.jdbc.OracleTypes");
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName("oracle.jdbc.driver.OracleTypes");
            } catch (ClassNotFoundException e2) {
                throw new SQLDialectException(String.format("Unable to locate OracleTypes class using either known FQN [%s, %s]", new Object[]{"oracle.jdbc.OracleTypes", "oracle.jdbc.driver.OracleTypes"}), e);
            }
        }
    }


    public int getOracleCursorTypeSqlType() {
        return this.oracleCursorTypeSqlType;
    }
}
