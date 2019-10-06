package com.jn.sqlhelper.common.ddlmodel;

import com.jn.sqlhelper.common.utils.ThrowableFunction;
import com.jn.sqlhelper.common.utils.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;

public class DatabaseDescription {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseDescription.class);
    private DatabaseMetaData dbMetaData;

    public DatabaseDescription(DatabaseMetaData databaseMetaData) {
        this.dbMetaData = databaseMetaData;
        init();
    }

    private void init() {
        parseAllProceduresAreCallable();
    }

    private boolean allProceduresAreCallable = false;

    private void parseAllProceduresAreCallable() {
        this.allProceduresAreCallable = Throwables.ignoreThrowable(logger, false, new ThrowableFunction<Object, Boolean>() {
            @Override
            public Boolean doFun(Object o) throws Throwable {
                return dbMetaData.allProceduresAreCallable();
            }
        }, null);
    }

    public boolean allProceduresAreCallable() {
        return allProceduresAreCallable;
    }
}
