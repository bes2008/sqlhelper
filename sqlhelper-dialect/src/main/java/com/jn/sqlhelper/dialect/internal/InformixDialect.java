
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

package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.annotation.Driver;
import com.jn.sqlhelper.dialect.internal.limit.LimitOnlyLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.SkipLimitHandler;
import com.jn.sqlhelper.dialect.urlparser.InformixUrlParser;

/**
 * https://www.ibm.com/support/knowledgecenter/en/SSGU8G_11.50.0/com.ibm.sqls.doc/ids_sqs_0987.htm
 */

@Name("informix")
@Driver("com.informix.jdbc.IfxDriver")
public class InformixDialect extends AbstractDialect {
    public InformixDialect() {
        super();
        setDelegate(new Informix10Dialect());
        setUrlParser(new InformixUrlParser());
    }

    public InformixDialect(java.sql.Driver driver) {
        super();
        if (driver.getMajorVersion() >= 4) {
            setDelegate(new Informix10Dialect());
        } else {
            setDelegate(new Informix9Dialect());
        }
        setUrlParser(new InformixUrlParser());
    }

    @Override
    public boolean isSupportsLimit() {
        return getRealDialect().isSupportsLimit();
    }

    @Override
    public boolean isUseMaxForLimit() {
        return getRealDialect().isUseMaxForLimit();
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return getRealDialect().isSupportsLimitOffset();
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return getRealDialect().isSupportsVariableLimit();
    }

    static class Informix9Dialect extends AbstractDialect {
        public Informix9Dialect() {
            super();
            this.setLimitHandler(new LimitOnlyLimitHandler());
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

    static class Informix10Dialect extends AbstractDialect {
        public Informix10Dialect() {
            super();
            this.setLimitHandler(new SkipLimitHandler());
        }

        @Override
        public boolean isSupportsLimit() {
            return true;
        }

        @Override
        public boolean isUseMaxForLimit() {
            return false;
        }

        @Override
        public boolean isSupportsLimitOffset() {
            return true;
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
}
