package com.jn.sqlhelper.common.ddl.model;

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
        parseSupportsMixedCaseIdentifiers();
        parseSupportsMixedCaseQuotedIdentifiers();
        parseSupportsSchemasInTableDefinitions();
        parseSupportsSchemasInIndexDefinitions();
        parseSupportsCatalogsInTableDefinitions();
        parseSupportsCatalogsInIndexDefinitions();
        parseCatalogSeparator();
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

    /**
     * Retrieves whether the current user can call all the procedures
     * returned by the method <code>getProcedures</code>.
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean allProceduresAreCallable() {
        return allProceduresAreCallable;
    }

    private boolean supportsMixedCaseIdentifiers = false;

    private void parseSupportsMixedCaseIdentifiers() {
        this.supportsMixedCaseIdentifiers = Throwables.ignoreThrowable(logger, false, new ThrowableFunction<Object, Boolean>() {
            @Override
            public Boolean doFun(Object o) throws Throwable {
                return dbMetaData.supportsMixedCaseIdentifiers();
            }
        }, null);
    }

    /**
     * Retrieves whether this database treats mixed case unquoted SQL identifiers as
     * case sensitive and as a result stores them in mixed case.
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean supportsMixedCaseIdentifiers() {
        return supportsMixedCaseIdentifiers;
    }

    private boolean supportsMixedCaseQuotedIdentifiers = false;

    private void parseSupportsMixedCaseQuotedIdentifiers() {
        this.supportsMixedCaseQuotedIdentifiers = Throwables.ignoreThrowable(logger, false, new ThrowableFunction<Object, Boolean>() {
            @Override
            public Boolean doFun(Object o) throws Throwable {
                return dbMetaData.supportsMixedCaseQuotedIdentifiers();
            }
        }, null);
    }

    /**
     * Retrieves whether this database treats mixed case quoted SQL identifiers as
     * case sensitive and as a result stores them in mixed case.
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean supportsMixedCaseQuotedIdentifiers() {
        return supportsMixedCaseQuotedIdentifiers;
    }

    private boolean supportsSchemasInTableDefinitions;

    private void parseSupportsSchemasInTableDefinitions() {
        this.supportsSchemasInTableDefinitions = Throwables.ignoreThrowable(logger, false, new ThrowableFunction<Object, Boolean>() {
            @Override
            public Boolean doFun(Object o) throws Throwable {
                return dbMetaData.supportsSchemasInTableDefinitions();
            }
        }, null);
    }

    /**
     * Retrieves whether a schema name can be used in a table definition statement.
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean supportsSchemasInTableDefinitions() {
        return supportsSchemasInTableDefinitions;
    }

    private boolean supportsSchemasInIndexDefinitions;

    private void parseSupportsSchemasInIndexDefinitions() {
        this.supportsSchemasInIndexDefinitions = Throwables.ignoreThrowable(logger, false, new ThrowableFunction<Object, Boolean>() {
            @Override
            public Boolean doFun(Object o) throws Throwable {
                return dbMetaData.supportsSchemasInIndexDefinitions();
            }
        }, null);
    }

    /**
     * Retrieves whether a schema name can be used in an index definition statement.
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean supportsSchemasInIndexDefinitions() {
        return supportsSchemasInIndexDefinitions;
    }

    private boolean supportsCatalogsInTableDefinitions;

    private void parseSupportsCatalogsInTableDefinitions() {
        this.supportsCatalogsInTableDefinitions = Throwables.ignoreThrowable(logger, false, new ThrowableFunction<Object, Boolean>() {
            @Override
            public Boolean doFun(Object o) throws Throwable {
                return dbMetaData.supportsCatalogsInTableDefinitions();
            }
        }, null);
    }

    /**
     * Retrieves whether a catalog name can be used in a table definition statement.
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean supportsCatalogsInTableDefinitions() {
        return supportsCatalogsInTableDefinitions;
    }

    private boolean supportsCatalogsInIndexDefinitions;

    private void parseSupportsCatalogsInIndexDefinitions() {
        this.supportsCatalogsInIndexDefinitions = Throwables.ignoreThrowable(logger, false, new ThrowableFunction<Object, Boolean>() {
            @Override
            public Boolean doFun(Object o) throws Throwable {
                return dbMetaData.supportsCatalogsInIndexDefinitions();
            }
        }, null);
    }

    /**
     * Retrieves whether a catalog name can be used in an index definition statement.
     *
     * @return <code>true</code> if so; <code>false</code> otherwise
     */
    public boolean supportsCatalogsInIndexDefinitions() {
        return supportsCatalogsInIndexDefinitions;
    }

    public DatabaseMetaData getDbMetaData() {
        return dbMetaData;
    }

    private String catalogSeparator;

    private void parseCatalogSeparator() {
        this.catalogSeparator = Throwables.ignoreThrowable(logger, ".", new ThrowableFunction<Object, String>() {
            @Override
            public String doFun(Object o) throws Throwable {
                return dbMetaData.getCatalogSeparator();
            }
        }, null);
    }

    public String getCatalogSeparator() {
        return this.catalogSeparator;
    }
}
