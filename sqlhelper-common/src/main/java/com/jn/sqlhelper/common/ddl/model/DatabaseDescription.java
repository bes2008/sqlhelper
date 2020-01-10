package com.jn.sqlhelper.common.ddl.model;

import com.jn.langx.util.ThrowableFunction;
import com.jn.langx.util.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;

public class DatabaseDescription {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseDescription.class);
    private DatabaseMetaData dbMetaData;

    public DatabaseDescription(DatabaseMetaData databaseMetaData) {
        this.dbMetaData = databaseMetaData;
    }

    private Boolean allProceduresAreCallable;

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
        if (allProceduresAreCallable == null) {
            parseAllProceduresAreCallable();
        }
        return allProceduresAreCallable;
    }

    private Boolean supportsMixedCaseIdentifiers;

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
        if (supportsMixedCaseIdentifiers == null) {
            parseSupportsMixedCaseIdentifiers();
        }
        return supportsMixedCaseIdentifiers;
    }

    private Boolean supportsMixedCaseQuotedIdentifiers;

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
        if (supportsMixedCaseQuotedIdentifiers == null) {
            parseSupportsMixedCaseQuotedIdentifiers();
        }
        return supportsMixedCaseQuotedIdentifiers;
    }

    private Boolean supportsSchemasInTableDefinitions;

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
        if (supportsSchemasInTableDefinitions == null) {
            parseSupportsSchemasInTableDefinitions();
        }
        return supportsSchemasInTableDefinitions;
    }

    private Boolean supportsSchemasInIndexDefinitions;

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
        if (supportsSchemasInIndexDefinitions == null) {
            parseSupportsSchemasInIndexDefinitions();
        }
        return supportsSchemasInIndexDefinitions;
    }

    private Boolean supportsCatalogsInTableDefinitions;

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
        if (supportsCatalogsInTableDefinitions) {
            parseSupportsCatalogsInTableDefinitions();
        }
        return supportsCatalogsInTableDefinitions;
    }

    private Boolean supportsCatalogsInIndexDefinitions;

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
        if (supportsCatalogsInIndexDefinitions == null) {
            parseSupportsCatalogsInIndexDefinitions();
        }
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

    /**
     * Retrieves the <code>String</code> that this database uses as the
     * separator between a catalog and table name.
     *
     * @return the separator string
     */
    public String getCatalogSeparator() {
        if (catalogSeparator == null) {
            parseCatalogSeparator();
        }
        return this.catalogSeparator;
    }

    private Boolean isCatalogAtStart = null;

    private void parseIsCatalogAtStart() {
        this.isCatalogAtStart = Throwables.ignoreThrowable(logger, true, new ThrowableFunction<Object, Boolean>() {
            @Override
            public Boolean doFun(Object o) throws Throwable {
                return dbMetaData.isCatalogAtStart();
            }
        }, null);
    }

    /**
     * Retrieves whether a catalog appears at the start of a fully qualified
     * table name.  If not, the catalog appears at the end.
     *
     * @return <code>true</code> if the catalog name appears at the beginning
     * of a fully qualified table name; <code>false</code> otherwise
     */
    public boolean isCatalogAtStart() {
        if (isCatalogAtStart == null) {
            parseIsCatalogAtStart();
        }
        return this.isCatalogAtStart;
    }

    private Boolean isSupportsBatchUpdates = null;

    /**
     * Retrieves whether this database supports batch updates.
     *
     * @return <code>true</code> if this database supports batch updates;
     * <code>false</code> otherwise
     */
    public boolean supportsBatchUpdates() {
        if (isSupportsBatchUpdates == null) {
            parseSupportsBatchUpdates();
        }
        return isSupportsBatchUpdates;
    }

    private void parseSupportsBatchUpdates() {
        this.isSupportsBatchUpdates = Throwables.ignoreThrowable(logger, true, new ThrowableFunction<Object, Boolean>() {
            @Override
            public Boolean doFun(Object o) throws Throwable {
                return dbMetaData.supportsBatchUpdates();
            }
        }, null);
    }
}
