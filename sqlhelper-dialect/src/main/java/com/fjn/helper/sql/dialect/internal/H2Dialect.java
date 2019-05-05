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

import com.fjn.helper.sql.dialect.internal.limit.LimitHelper;
import com.fjn.helper.sql.dialect.RowSelection;
import com.fjn.helper.sql.dialect.internal.limit.AbstractLimitHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class H2Dialect extends AbstractDialect {
    private static final Logger LOG = LoggerFactory.getLogger(H2Dialect.class);

    public H2Dialect() {
        super();
        setLimitHandler(new AbstractLimitHandler() {
            @Override
            public String processSql(String sql, RowSelection selection) {
                boolean hasOffset = LimitHelper.hasFirstRow(selection);
                return getLimitString(sql, hasOffset);
            }

            @Override
            public String getLimitString(String sql, boolean hasOffset) {
                return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
            }
        });

        String querySequenceString = "select sequence_name from information_schema.sequences";
        try {
            Class h2ConstantsClass = Class.forName("org.h2.engine.Constants");
            int majorVersion = ((Integer) h2ConstantsClass.getDeclaredField("VERSION_MAJOR").get(null)).intValue();
            int minorVersion = ((Integer) h2ConstantsClass.getDeclaredField("VERSION_MINOR").get(null)).intValue();
            int buildId = ((Integer) h2ConstantsClass.getDeclaredField("BUILD_ID").get(null)).intValue();
            if (buildId < 32) {
                querySequenceString = "select name from information_schema.sequences";
            }
            if ((majorVersion <= 1) && (minorVersion <= 2) && (buildId < 139)) {
            }


            return;
        } catch (Exception localException) {
        }
    }


    public boolean isSupportsLimit() {
        return true;
    }


    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }
}
