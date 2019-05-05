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

import java.util.Locale;


public class HSQLDialect extends AbstractDialect {
    private final class HSQLLimitHandler extends AbstractLimitHandler {
        private HSQLLimitHandler() {
        }

        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            return getLimitString(sql, hasOffset);
        }

        @Override
        public String getLimitString(String sql, boolean hasOffset) {
            if (HSQLDialect.this.hsqldbVersion < 200) {
                return new StringBuilder(sql.length() + 10).append(sql).insert(sql.toLowerCase(Locale.ROOT).indexOf("select") + 6, hasOffset ? " limit ? ?" : " top ?").toString();
            }
            return sql + (hasOffset ? " offset ? limit ?" : " limit ?");
        }
    }


    private int hsqldbVersion = 180;


    public HSQLDialect() {
        super();
        try {
            Class props = Class.forName("org.hsqldb.persist.HsqlDatabaseProperties");
            String versionString = (String) props.getDeclaredField("THIS_VERSION").get(null);

            this.hsqldbVersion = (Integer.parseInt(versionString.substring(0, 1)) * 100);
            this.hsqldbVersion += Integer.parseInt(versionString.substring(2, 3)) * 10;
            this.hsqldbVersion += Integer.parseInt(versionString.substring(4, 5));
        } catch (Throwable localThrowable) {
        }

        setLimitHandler(new HSQLLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return this.hsqldbVersion < 200;
    }
}
