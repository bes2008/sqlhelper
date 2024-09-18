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
    }

    public InformixDialect(java.sql.Driver driver) {
        super();
        if (driver.getMajorVersion() >= 4) {
            setDelegate(new Informix10Dialect());
        } else {
            setDelegate(new Informix9Dialect());
        }
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
