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

public class DerbyDialect extends AbstractDialect {
    private int driverVersionMajor;
    private int driverVersionMinor;

    public DerbyDialect() {
        super();
        determineDriverVersion();
        setLimitHandler(new DerbyLimitHandler());
    }

    private void determineDriverVersion() {
        try {
            Class localClass = Class.forName("org.apache.derby.tools.sysinfo");
        } catch (Exception e) {
            this.driverVersionMajor = -1;
            this.driverVersionMinor = -1;
        }
    }

    private boolean isTenPointFiveReleaseOrNewer() {
        return (this.driverVersionMajor > 10) || ((this.driverVersionMajor == 10) && (this.driverVersionMinor >= 5));
    }

    @Override
    public boolean isSupportsLimit() {
        return isTenPointFiveReleaseOrNewer();
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return isTenPointFiveReleaseOrNewer();
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return false;
    }

    private boolean hasForUpdateClause(int forUpdateIndex) {
        return forUpdateIndex >= 0;
    }

    private boolean hasWithClause(String normalizedSelect) {
        return normalizedSelect.startsWith("with ", normalizedSelect.length() - 7);
    }

    private int getWithIndex(String querySelect) {
        int i = querySelect.lastIndexOf("with ");
        if (i < 0) {
            i = querySelect.lastIndexOf("WITH ");
        }
        return i;
    }


    private final class DerbyLimitHandler
            extends AbstractLimitHandler {
        private DerbyLimitHandler() {
        }

        @Override
        public String processSql(String sql, RowSelection selection) {
            StringBuilder sb = new StringBuilder(sql.length() + 50);
            String normalizedSelect = sql.toLowerCase(Locale.ROOT).trim();
            int forUpdateIndex = normalizedSelect.lastIndexOf("for update");

            if (DerbyDialect.this.hasForUpdateClause(forUpdateIndex)) {
                sb.append(sql.substring(0, forUpdateIndex - 1));
            } else if (DerbyDialect.this.hasWithClause(normalizedSelect)) {
                sb.append(sql.substring(0, DerbyDialect.this.getWithIndex(sql) - 1));
            } else {
                sb.append(sql);
            }

            if (LimitHelper.hasFirstRow(selection)) {
                sb.append(" offset ").append(selection.getOffset()).append(" rows fetch next ");
            } else {
                sb.append(" fetch first ");
            }

            sb.append(getMaxOrLimit(selection)).append(" rows only");

            if (DerbyDialect.this.hasForUpdateClause(forUpdateIndex)) {
                sb.append(' ');
                sb.append(sql.substring(forUpdateIndex));
            } else if (DerbyDialect.this.hasWithClause(normalizedSelect)) {
                sb.append(' ').append(sql.substring(DerbyDialect.this.getWithIndex(sql)));
            }
            return sb.toString();
        }

        @Override
        public String getLimitString(String query, int offset, int limit) {
            StringBuilder sb = new StringBuilder(query.length() + 50);
            String normalizedSelect = query.toLowerCase(Locale.ROOT).trim();
            int forUpdateIndex = normalizedSelect.lastIndexOf("for update");

            if (DerbyDialect.this.hasForUpdateClause(forUpdateIndex)) {
                sb.append(query.substring(0, forUpdateIndex - 1));
            } else if (DerbyDialect.this.hasWithClause(normalizedSelect)) {
                sb.append(query.substring(0, DerbyDialect.this.getWithIndex(query) - 1));
            } else {
                sb.append(query);
            }

            if (offset == 0) {
                sb.append(" fetch first ");
            } else {
                sb.append(" offset ").append(offset).append(" rows fetch next ");
            }

            sb.append(limit).append(" rows only");

            if (DerbyDialect.this.hasForUpdateClause(forUpdateIndex)) {
                sb.append(' ');
                sb.append(query.substring(forUpdateIndex));
            } else if (DerbyDialect.this.hasWithClause(normalizedSelect)) {
                sb.append(' ').append(query.substring(DerbyDialect.this.getWithIndex(query)));
            }
            return sb.toString();
        }
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }
}
