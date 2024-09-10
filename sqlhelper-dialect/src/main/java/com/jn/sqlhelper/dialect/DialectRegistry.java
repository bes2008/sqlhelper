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

package com.jn.sqlhelper.dialect;

import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.text.properties.Props;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.Holder;
import com.jn.sqlhelper.common.ddl.SQLSyntaxCompatTable;
import com.jn.sqlhelper.common.utils.Connections;
import com.jn.sqlhelper.dialect.annotation.Driver;
import com.jn.sqlhelper.dialect.annotation.SyntaxCompat;
import com.jn.sqlhelper.dialect.internal.*;
import com.jn.sqlhelper.dialect.urlparser.DatabaseInfo;
import com.jn.sqlhelper.dialect.urlparser.JdbcUrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class DialectRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DialectRegistry.class);
    private static final Map<String, Dialect> nameToDialectMap = new TreeMap<String, Dialect>();
    private static final Map<String, String> classNameToNameMap = new TreeMap<String, String>();
    // key:DatabaseMetaData.getProduceName() + getDriver();
    private static final Map<String, Holder<Dialect>> dbToDialectMap = new HashMap<String, Holder<Dialect>>();
    private static final Properties vendorDatabaseNameMappings = new Properties();
    private static final DialectRegistry registry = new DialectRegistry();

    static {
        loadDatabaseNameMappings();
        registerBuiltinDialects();
        loadCustomDialects();
    }

    private DialectRegistry() {
    }

    public static DialectRegistry getInstance() {
        return registry;
    }

    private static String databaseIdStringLowerCase(DatabaseMetaData databaseMetaData) {
        return databaseIdString(databaseMetaData).toLowerCase();
    }

    public static String databaseIdString(DatabaseMetaData databaseMetaData) {
        try {
            return databaseMetaData.getDatabaseProductName();
        } catch (SQLException ex) {
            try {
                return databaseMetaData.getDriverName().toLowerCase() + " version: " + databaseMetaData.getDriverVersion().toLowerCase();
            } catch (SQLException ex1) {
                // ignore it
            }
        }
        try {
            return databaseMetaData.getURL();
        } catch (SQLException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return databaseMetaData.getClass().getCanonicalName();
    }

    private static void loadCustomDialects() {
        loadCustomDialects(DialectRegistry.class.getClassLoader());
    }

    public static void loadCustomDialects(ClassLoader classLoader) {
        ServiceLoader<AbstractDialect> serviceLoader = ServiceLoader.load(AbstractDialect.class, classLoader);
        for (AbstractDialect dialect : serviceLoader) {
            registerDialectByClass(dialect.getClass(), dialect);
        }
    }

    private static void registerBuiltinDialects() {
        logger.info("Start to register builtin dialects");

        final Class<? extends Dialect>[] dialects = (Class<? extends Dialect>[]) new Class[]{

                AccessDialect.class,
                ActorDBDialect.class,
                AgensGraphDialect.class,
                AliSQLDialect.class,
                AltibaseDialect.class,
                AntDBDialect.class,
                AuroraDialect.class,
                ArgoDBDialect.class,
                AzureDialect.class,
                As400Dialect.class,

                BesMagicDataDialect.class,
                BigObjectDialect.class,
                BrytlytDialect.class,

                CacheDialect.class,
                CitusDialect.class,
                ClickHouseDialect.class,
                ClustrixDialect.class,
                CobolDialect.class,
                CockroachDialect.class,
                ComDB2Dialect.class,
                CovenantSQLDialect.class,
                CrateDialect.class,
                CTreeDialect.class,
                CubridDialect.class,

                DatabricksDialect.class,
                DB2Dialect.class,
                DbfDialect.class,
                DerbyDialect.class,
                DmDialect.class,
                DorisDialect.class,
                DrillDialect.class,
                DuckDBDialect.class,

                ElasticsearchDialect.class,
                EsgynDBDialect.class,
                ExasolDialect.class,

                FileMakerDialect.class,
                FirebirdDialect.class,

                GaussDbDialect.class,
                GBaseDialect.class,
                GBase8sDialect.class,
                GoldenDBDialect.class,
                GreenplumDialect.class,

                H2Dialect.class,
                HANADialect.class,
                HawqDialect.class,
                HerdDBDialect.class,
                HhDbDialect.class,
                HighGoDialect.class,
                HiveDialect.class,
                HSQLDialect.class,

                IgniteDialect.class,
                ImpalaDialect.class,
                InformixDialect.class,
                IngresDialect.class,
                InterbaseDialect.class,
                IrisDialect.class,

                JDataStoreDialect.class,

                KarelDBDialect.class,
                KDBDialect.class,
                KingbaseDialect.class,
                KingDBDialect.class,
                KineticaDialect.class,
                KognitioDialect.class,

                LeanXcaleDialect.class,
                LinterDialect.class,

                MariaDBDialect.class,
                MaxComputeDialect.class,
                MaxDBDialect.class,
                MckoiDialect.class,
                MemSQLDialect.class,
                MimerSQLDialect.class,
                MonetDialect.class,
                ModeShapeDialect.class,
                MSQLDialect.class,
                MySQLDialect.class,

                Neo4jDialect.class,
                NetezzaDialect.class,
                NexusDBDialect.class,
                NuodbDialect.class,

                OBaseDialect.class,
                OmnisciDialect.class,
                OpenbaseDialect.class,
                OpenEdgeDialect.class,
                OracleDialect.class,
                OrientDBDialect.class,
                OscarDialect.class,

                ParadoxDialect.class,
                PerconaMysqlDialect.class,
                PhoenixDialect.class,
                PointbaseDialect.class,
                PostgreSQLDialect.class,
                PrestoDialect.class,

                RadonDBDialect.class,
                RaimaDialect.class,
                RBaseDialect.class,
                RDMSOS2200Dialect.class,
                RedshiftDialect.class,

                SadasDialect.class,
                SequoiaDBDialect.class,
                SinoDBDialect.class,
                SmallDialect.class,
                SnappyDataDialect.class,
                SnowflakeDialect.class,
                SpliceMachineDialect.class,
                SQLiteDialect.class,
                SQLServerDialect.class,
                SQLServerDialect.SQLServer2000Dialect.class,
                SQLServerDialect.SQLServer2005Dialect.class,
                SQLServerDialect.SQLServer2008Dialect.class,
                SQLServerDialect.SQLServer2012Dialect.class,
                SQLServerDialect.SQLServer2014Dialect.class,
                SQLServerDialect.SQLServer2016Dialect.class,
                SQLServerDialect.SQLServer2017Dialect.class,
                SQReamDialect.class,

                TajoDialect.class,
                TeradataDialect.class,
                TiDBDialect.class,
                TimesTenDialect.class,
                TrafodionDialect.class,
                TransbaseDialect.class,
                TrinoDialect.class,

                UxDBDialect.class,

                ValentinaDialect.class,
                VerticaDialect.class,
                VirtuosoDialect.class,
                VistaDBDialect.class,
                VoltDBDialect.class,

                XtremeSQLDialect.class,
                XuguDialect.class,
                XCloudDBDialect.class,

                YaacomoDialect.class,
                YugabyteDBDialect.class
        };

        for (Class<? extends Dialect> clazz : Arrays.asList(dialects)) {
            registerDialectByClass(clazz, null);
        }

        logger.info("Registered dialects: {}", nameToDialectMap.keySet());
    }

    private static void loadDatabaseNameMappings() {
        logger.info("Start to load database mappings (product or vendor name => dialect name)");

        try {
            Properties props = Props.loadFromClasspath("/sqlhelper-dialect-database.properties");
            vendorDatabaseNameMappings.putAll(props);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static Properties getVendorDatabaseIdMappings() {
        return vendorDatabaseNameMappings;
    }

    public static void setDatabaseName(String keywordsInDriver, String databaseId) {
        setDatabaseNameIfAbsent(keywordsInDriver, databaseId);
    }

    @Deprecated
    public static void setDatabaseId(String keywordsInDriver, String databaseId) {
        setDatabaseName(keywordsInDriver, databaseId);
    }

    public static void setDatabaseNameIfAbsent(String keywordsInDriver, String databaseId) {
        if (!vendorDatabaseNameMappings.containsKey(keywordsInDriver)) {
            vendorDatabaseNameMappings.setProperty(keywordsInDriver, databaseId);
        }
    }

    /**
     * 由 setDatabaseNameIfAbsent 替代
     */
    @Deprecated
    public static void setDatabaseIdIfAbsent(String keywordsInDriver, String databaseId) {
        setDatabaseNameIfAbsent(keywordsInDriver, databaseId);
    }

    public static String guessDatabaseId(DataSource dataSource) {
        if (dataSource == null) {
            throw new NullPointerException("dataSource cannot be null");
        }
        try {
            return guessDatabaseId(Connections.getDatabaseProductName(dataSource));
        } catch (Exception e) {
            logger.error("Could not get a databaseId from dataSource", e);
        }
        return null;
    }

    /**
     * guess based productName, url, driver etc
     *
     * @return database id
     */
    public static String guessDatabaseName(final String productName) {
        return guessDatabaseId(productName);
    }

    @Deprecated
    public static String guessDatabaseId(final String productName) {

        if (productName == null) {
            return null;
        }
        String tmpProductName = productName.toLowerCase();
        // if arg is a url
        int urlProtocolIndex = tmpProductName.indexOf("://");
        if (urlProtocolIndex != -1) {
            DatabaseInfo databaseInfo = new JdbcUrlParser().parse(productName);
            if (databaseInfo != null && !DatabaseInfo.UNKNOWN.equals(databaseInfo.getVendor().toLowerCase())) {
                tmpProductName = databaseInfo.getVendor().toLowerCase();
            } else {
                tmpProductName = tmpProductName.substring(0, urlProtocolIndex);
            }
        }
        int urlPropertyFragmentIndex = tmpProductName.indexOf("?");
        if (urlPropertyFragmentIndex != -1) {
            DatabaseInfo databaseInfo = new JdbcUrlParser().parse(productName);
            if (databaseInfo != null && !DatabaseInfo.UNKNOWN.equals(databaseInfo.getVendor().toLowerCase())) {
                tmpProductName = databaseInfo.getVendor().toLowerCase();
            } else {
                tmpProductName = tmpProductName.substring(0, urlPropertyFragmentIndex);
            }
        }

        String[] tokens = Strings.split(tmpProductName, ":");

        final Set<String> productKeywords = vendorDatabaseNameMappings.stringPropertyNames();
        final Holder<String> matchedProductHolder = new Holder<String>();
        Pipeline.of(tokens).filter(new Predicate<String>() {
            @Override
            public boolean test(String token) {
                return !Strings.equalsIgnoreCase(token, "jdbc");
            }
        }).forEach(new Consumer<String>() {
            @Override
            public void accept(final String token) {
                String matchedProductKeyword = Pipeline.of(productKeywords)
                        .findFirst(new Predicate<String>() {
                            @Override
                            public boolean test(String productKeyword) {
                                return token.contains(productKeyword.toLowerCase());
                            }
                        });
                if (Strings.isNotBlank(matchedProductKeyword)) {
                    matchedProductHolder.set(matchedProductKeyword);
                }
            }
        }, new Predicate<String>() {
            @Override
            public boolean test(String token) {
                return !matchedProductHolder.isEmpty();
            }
        });

        String bestProductKeyword = matchedProductHolder.get();

        if (Strings.isNotBlank(bestProductKeyword)) {
            return vendorDatabaseNameMappings.getProperty(bestProductKeyword);
        }

        if (classNameToNameMap.containsKey(tmpProductName)) {
            return classNameToNameMap.get(tmpProductName);
        }
        return null;
    }

    private static Class<? extends Dialect> loadDialectClass(final String className) throws ClassNotFoundException {
        return (Class<? extends Dialect>) loadImplClass(className, Dialect.class);
    }

    private static Class<? extends java.sql.Driver> loadDriverClass(final String className) throws ClassNotFoundException {
        return (Class<? extends java.sql.Driver>) loadImplClass(className, java.sql.Driver.class);
    }

    private static Class loadImplClass(final String className, final Class superClass) throws ClassNotFoundException {
        Class clazz = null;
        try {
            clazz = Class.forName(className, true, DialectRegistry.class.getClassLoader());
        } catch (ClassNotFoundException ex) {
            // NOOP
        }
        if (clazz == null) {
            clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        }
        if (superClass.isAssignableFrom(clazz)) {
            return clazz;
        }
        final String error = "Class " + Reflects.getFQNClassName(clazz) + " is not cast to " + Reflects.getFQNClassName(superClass);
        throw new ClassCastException(error);
    }

    private static Dialect registerDialectByClass(final Class<? extends Dialect> clazz) {
        return registerDialectByClass(clazz, null);
    }

    private static Dialect registerDialectByClass(@NonNull final Class<? extends Dialect> dialectClass, @Nullable Dialect dialect) {
        Preconditions.checkNotNull(dialectClass);
        // step 1: 生成 database id
        final Name nameAnno = Reflects.<Name>getAnnotation(dialectClass, Name.class);
        String name;
        if (nameAnno != null) {
            name = nameAnno.value();
            if (Strings.isBlank(name)) {
                throw new IllegalStateException("@Name is empty in class" + Reflects.getFQNClassName(dialectClass));
            }
        } else {
            final String simpleClassName = dialectClass.getSimpleName().toLowerCase();
            name =  Strings.replace(simpleClassName, "dialect", "");
        }
        if (dialect == null) {
            final Driver driverAnno = Reflects.<Driver>getAnnotation(dialectClass, Driver.class);
            Class<? extends java.sql.Driver> driverClass = null;
            Constructor<? extends Dialect> driverConstructor = null;
            if (driverAnno != null) {
                String[] driverClassNames = Pipeline.of(driverAnno.value())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String driverClassName) {
                                return Strings.isNotBlank(driverClassName);
                            }
                        }).toArray(String[].class);

                if (Objs.isEmpty(driverClassNames)) {
                    throw new IllegalStateException("@Driver is empty in class" + Reflects.getFQNClassName(dialectClass));
                }
                for (int i = 0; i < driverClassNames.length; i++) {
                    String driverClassName = driverClassNames[i];
                    try {
                        driverClass = loadDriverClass(driverClassName);
                        try {
                            driverConstructor = dialectClass.getDeclaredConstructor(java.sql.Driver.class);
                        } catch (Throwable ex) {
                            logger.info("Can't find the driver based constructor for dialect {}", name);
                        }
                    } catch (Throwable ex) {
                        logger.info("Can't find driver class {} for {} dialect", driverClassName, name);
                    }

                    if (driverClass != null) {
                        break;
                    }
                }
            }
            if (driverClass == null || driverConstructor == null) {
                try {
                    try {
                        dialect = dialectClass.newInstance();
                    } catch (InstantiationException e2) {
                        final String error = StringTemplates.formatWithPlaceholder("Class {}  need a <init>()", Reflects.getFQNClassName(dialectClass));
                        throw new ClassFormatError(error);
                    } catch (IllegalAccessException e3) {
                        final String error = StringTemplates.formatWithPlaceholder("Class {}  need a <init>()", Reflects.getFQNClassName(dialectClass));
                        throw new ClassFormatError(error);
                    }
                } catch (Throwable ex) {
                    logger.error("Register dialect {} fail: {}", name, ex.getMessage(), ex);
                }
            } else {
                try {
                    try {
                        final Class<? extends java.sql.Driver> expectDriverClass = driverClass;
                        java.sql.Driver driver = Pipeline.<java.sql.Driver>of(DriverManager.getDrivers()).findFirst(new Predicate<java.sql.Driver>() {
                            @Override
                            public boolean test(java.sql.Driver d) {
                                return expectDriverClass.isInstance(d);
                            }
                        });
                        if (driver != null) {
                            driverConstructor.setAccessible(true);
                            dialect = driverConstructor.newInstance(driver);
                        }
                    } catch (InstantiationException e2) {
                        final String error = StringTemplates.formatWithPlaceholder("Class {}  need a <init>(Driver)", Reflects.getFQNClassName(dialectClass));
                        throw new ClassFormatError(error);
                    } catch (IllegalAccessException e3) {
                        final String error = StringTemplates.formatWithPlaceholder("Class {} need a public <init>(Driver", Reflects.getFQNClassName(dialectClass));
                        throw new ClassFormatError(error);
                    } catch (InvocationTargetException e) {
                        logger.error("Register dialect {} fail: {}", name, e.getMessage(), e);
                    }
                } catch (Throwable ex) {
                    logger.error("Register dialect {} fail: {}", name, ex.getMessage(), ex);
                }
            }
        }
        if (dialect != null) {
            DialectRegistry.nameToDialectMap.put(name, dialect);
            DialectRegistry.classNameToNameMap.put(dialectClass.getCanonicalName(), name);
            setDatabaseName(name, name);
        }

        // step 2: 扫描兼容性
        if (dialect != null) {
            SyntaxCompat syntaxCompat = Reflects.<SyntaxCompat>getAnnotation(dialectClass, SyntaxCompat.class);
            if (syntaxCompat != null) {
                if (Emptys.isNotEmpty(syntaxCompat.value())) {
                    SQLSyntaxCompatTable.getInstance().register(name, syntaxCompat.value());
                }
            }
        }

        return dialect;
    }

    public Collection<Dialect> getDialects() {
        return nameToDialectMap.values();
    }

    public Dialect getDialectByClassName(final String className) {
        final String dialectName = DialectRegistry.classNameToNameMap.get(className);
        if (dialectName != null) {
            return this.getDialectByName(dialectName);
        }
        return null;
    }

    public Dialect getDialectByName(final String databaseId) {
        return DialectRegistry.nameToDialectMap.get(databaseId);
    }

    /**
     * @since 5.0.5
     * @param databaseId dialect or name
     * @return the dialect object
     */
    public Dialect gaussDialect(final String databaseId) {
        Dialect dialect = getDialectByName(databaseId);
        if(dialect == null && Strings.isNotBlank(databaseId)){
            String guessedDatabaseId = guessDatabaseId(databaseId);
            if(Strings.isNotEmpty(guessedDatabaseId)){
                dialect = getDialectByName(guessedDatabaseId);
            }
        }
        return dialect;
    }

    public Dialect getDialectByDatabaseMetadata(final DatabaseMetaData databaseMetaData) {
        Dialect dialect = null;
        if (databaseMetaData != null) {
            dialect = getDialectByResolutionInfo(new DatabaseMetaDataDialectResolutionInfoAdapter(databaseMetaData));
        }
        return dialect;
    }

    public Dialect getDialectByResolutionInfo(DialectResolutionInfo resolutionInfo) {
        Dialect dialect = null;
        if (resolutionInfo != null) {
            String databaseIdString = resolutionInfo.getDatabaseProductName();
            if (databaseIdString != null) {
                databaseIdString = Strings.lowerCase(resolutionInfo.getDatabaseProductName(), Locale.ROOT);
                Holder<Dialect> dialectHolder = dbToDialectMap.get(databaseIdString);
                if (dialectHolder != null) {
                    dialect = dialectHolder.get();
                }
                if (dialect == null) {
                    Enumeration<String> keys = (Enumeration<String>) vendorDatabaseNameMappings.propertyNames();
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement();
                        if (databaseIdString.contains(key.toLowerCase())) {
                            dialect = getDialectByName(vendorDatabaseNameMappings.getProperty(key));
                            if (dialect != null) {
                                dbToDialectMap.put(databaseIdString, new Holder<Dialect>(dialect));
                                break;
                            }
                        }
                    }
                }

                // sqlserver
                if (dialect == null) {
                    if (Strings.containsAny(databaseIdString.toLowerCase(), "sql server") || Strings.containsAny(databaseIdString.toLowerCase(), "sqlserver")) {
                        try {
                            String productionVersion = resolutionInfo.getDatabaseProductVersion();
                            if (Strings.isBlank(productionVersion)) {
                                productionVersion = resolutionInfo.getDatabaseMajorVersion() + "";
                            }
                            String tmpDatabaseId = SQLServerDialect.guessDatabaseId(productionVersion);
                            if (Emptys.isNotEmpty(tmpDatabaseId)) {
                                dialect = getDialectByName(vendorDatabaseNameMappings.getProperty(tmpDatabaseId));
                                if (dialect != null) {
                                    dbToDialectMap.put(databaseIdString, new Holder<Dialect>(dialect));
                                }
                            }
                        } catch (Throwable ex) {
                            // ignore it
                        }
                    }
                }
            }
        }
        return dialect;
    }

    public void registerDialectByClassName(final String dialectClassName) throws ClassNotFoundException {
        this.registerDialect(null, dialectClassName);
    }

    public void registerDialect(final String dialectName, final String className) throws ClassNotFoundException {
        final Class<? extends Dialect> clazz = loadDialectClass(className);
        try {
            final Dialect dialect = registerDialectByClass(clazz);
            if (!Strings.isBlank(dialectName) && dialect != null) {
                DialectRegistry.nameToDialectMap.put(dialectName, dialect);
            }
        } catch (Throwable ex) {
            DialectRegistry.logger.info(ex.getMessage(), ex);
        }
    }


}
