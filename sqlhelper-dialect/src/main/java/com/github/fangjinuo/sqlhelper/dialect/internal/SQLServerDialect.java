
/*
 * Copyright 2019 the original author or authors.
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

package com.github.fangjinuo.sqlhelper.dialect.internal;

import com.github.fangjinuo.sqlhelper.dialect.internal.limit.*;
import com.github.fangjinuo.sqlhelper.dialect.internal.urlparser.SqlServerUrlParser;


public class SQLServerDialect extends AbstractTransactSQLDialect {
    private static final int PARAM_LIST_SIZE_LIMIT = 2100;

    public SQLServerDialect() {
        super();
        setDelegate(new SQLServer2008Dialect());
        setUrlParser(new SqlServerUrlParser());
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return false;
    }

    class SQLServer2000Dialect extends AbstractTransactSQLDialect {
        private SQLServer2000Dialect() {
            setLimitHandler(new TopLimitHandler());
        }

        @Override
        public boolean isSupportsLimit() {
            return true;
        }

        @Override
        public boolean isUseMaxForLimit() {
            return true;
        }

        @Override
        public boolean isSupportsLimitOffset() {
            return false;
        }

        @Override
        public boolean isSupportsVariableLimit() {
            return false;
        }

        @Override
        public boolean isBindLimitParametersFirst() {
            return true;
        }
    }

    class SQLServer2005Dialect extends AbstractTransactSQLDialect {
        private SQLServer2005Dialect() {
            setLimitHandler(new SQLServer2005LimitHandler());
        }

        @Override
        public boolean isSupportsLimit() {
            return true;
        }

        @Override
        public boolean isUseMaxForLimit() {
            return true;
        }

        @Override
        public boolean isSupportsLimitOffset() {
            return true;
        }

        @Override
        public boolean isSupportsVariableLimit() {
            return true;
        }

        @Override
        protected char getBeforeQuote() {
            return '[';
        }

        @Override
        protected char getAfterQuote() {
            return ']';
        }
    }

    class SQLServer2008Dialect extends AbstractTransactSQLDialect {
        private SQLServer2008Dialect() {
            setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
        }

        @Override
        public boolean isSupportsLimit() {
            return true;
        }

        @Override
        public boolean isUseMaxForLimit() {
            return true;
        }

        @Override
        public boolean isSupportsLimitOffset() {
            return true;
        }

        @Override
        public boolean isSupportsVariableLimit() {
            return true;
        }

        @Override
        protected char getBeforeQuote() {
            return '[';
        }

        @Override
        protected char getAfterQuote() {
            return ']';
        }
    }

    class SQLServer2012Dialect extends SQLServer2008Dialect{
    }

    class SQLServer2017Dialect extends SQLServer2012Dialect{}

    @Override
    protected char getBeforeQuote() {
        return '[';
    }

    @Override
    protected char getAfterQuote() {
        return ']';
    }
}
