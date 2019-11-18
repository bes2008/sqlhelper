package com.jn.sqlhelper.cli.commands;

import com.jn.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationRepository;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.io.LineDelimiter;
import com.jn.langx.util.io.file.Files;
import com.jn.sqlhelper.common.connection.ConnectionFactory;
import com.jn.sqlhelper.common.connection.NamedConnectionConfiguration;
import com.jn.sqlhelper.common.ddl.dump.DatabaseLoader;
import com.jn.sqlhelper.common.ddl.model.DatabaseDescription;
import com.jn.sqlhelper.common.ddl.model.Index;
import com.jn.sqlhelper.common.ddl.model.Table;
import com.jn.sqlhelper.common.utils.Connections;
import com.jn.sqlhelper.common.utils.SQLs;
import com.jn.sqlhelper.dialect.ddl.generator.CommonTableGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

import static com.jn.sqlhelper.common.utils.SQLs.SQL_FILE_SUFFIX;

@ShellComponent
public class ShowDatabaseCommands {
    private static final Logger logger = LoggerFactory.getLogger(ShowDatabaseCommands.class);
    @Autowired
    DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration> repository;

    private Connection getConnectionByConnectionConfigurationId(String id) {
        NamedConnectionConfiguration configuration = repository.getById(id);
        Preconditions.checkNotNull(configuration, StringTemplates.formatWithPlaceholder("Can't find a connection configuration named {}", id));
        ConnectionFactory factory = new ConnectionFactory(configuration);
        return factory.getConnection();
    }

    @ShellMethod(key = "show tables", value = "Show table names")
    public List<String> getTableNames(
            @ShellOption(help = "the connection configuration name") String connectionName
    ) {
        Connection connection = getConnectionByConnectionConfigurationId(connectionName);
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            final DatabaseDescription databaseDescription = new DatabaseDescription(dbMetaData);

            List<Table> tables = new DatabaseLoader().loadTables(databaseDescription, Connections.getCatalog(connection), Connections.getSchema(connection), null);
            return Pipeline.of(tables).map(new Function<Table, String>() {
                @Override
                public String apply(Table table) {
                    return SQLs.getTableFQN(databaseDescription, table.getCatalog(), table.getSchema(), table.getName());
                }
            }).asList();
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        } finally {
            IOs.close(connection);
        }
    }

    @ShellMethod(key = "show table", value = "Show table detail")
    public Table getTable(@ShellOption(help = "the connection configuration name") String connectionName,
                          @ShellOption(help = "the table name") String table) {
        Connection connection = getConnectionByConnectionConfigurationId(connectionName);
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            return new DatabaseLoader().loadTable(new DatabaseDescription(dbMetaData), Connections.getCatalog(connection), Connections.getSchema(connection), table);
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        } finally {
            IOs.close(connection);
        }
    }

    @ShellMethod(key = "show indexes", value = "Show table index")
    public List<String> getIndexNames(
            @ShellOption(help = "the connection configuration name") String connectionName,
            @ShellOption(help = "the table name") String table) {
        Connection connection = getConnectionByConnectionConfigurationId(connectionName);

        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            DatabaseDescription databaseDescription = new DatabaseDescription(dbMetaData);
            List<Index> indexes = new DatabaseLoader().findTableIndexes(databaseDescription, Connections.getCatalog(connection), Connections.getSchema(connection), table);
            return Pipeline.of(indexes).map(new Function<Index, String>() {
                @Override
                public String apply(Index index) {
                    return index.getName() + "\t" + SQLs.getTableFQN(index.getCatalog(), index.getSchema(), index.getTableName());
                }
            }).asList();
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        } finally {
            IOs.close(connection);
        }
    }

    @ShellMethod(key = "show index", value = "Show index detail")
    public Index getIndex(@ShellOption(help = "the connection configuration name") String connectionName,
                          @ShellOption(help = "the table name") String table,
                          @ShellOption(help = "the index name") String index) {
        Connection connection = getConnectionByConnectionConfigurationId(connectionName);

        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            Table t = new DatabaseLoader().loadTable(new DatabaseDescription(dbMetaData), Connections.getCatalog(connection), Connections.getSchema(connection), table);
            return t.getIndex(index);
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        } finally {
            IOs.close(connection);
        }
    }

    @ShellMethod(key = "show ddl", value = "Show table DDL")
    public String getTableDDL(@ShellOption(help = "the connection configuration name") String connectionName,
                              @ShellOption(help = "the table name") String table) {
        Connection connection = getConnectionByConnectionConfigurationId(connectionName);
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            DatabaseDescription databaseDescription = new DatabaseDescription(dbMetaData);
            Table t = new DatabaseLoader().loadTable(databaseDescription, Connections.getCatalog(connection), Connections.getSchema(connection), table);
            Preconditions.checkNotNull(t, StringTemplates.formatWithPlaceholder("table {} is not exists", table));
            CommonTableGenerator generator = new CommonTableGenerator(databaseDescription);
            return generator.generate(t);
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        } finally {
            IOs.close(connection);
        }
    }

    @ShellMethod(key = "dump ddl", value = "Show table DDL")
    public String dumpTablesDDL(@ShellOption(help = "the connection configuration name") String connectionName,
                                @ShellOption(help = "the table name", defaultValue = "") String table,
                                @ShellOption(help = "the dump directory") String directory,
                                @ShellOption(help = "the dump filename") String filename,
                                @ShellOption(help = "postback to you", defaultValue = "false") boolean postback) {
        Connection connection = getConnectionByConnectionConfigurationId(connectionName);
        BufferedWriter bf = null;
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            DatabaseDescription databaseDescription = new DatabaseDescription(dbMetaData);
            table = Strings.getNullIfBlank(table);
            List<Table> ts = new DatabaseLoader().loadTables(databaseDescription, Connections.getCatalog(connection), Connections.getSchema(connection), table, true, true, true, true);
            Preconditions.checkNotNull(ts, StringTemplates.formatWithPlaceholder("table {} is not exists", table));

            if (!Strings.endsWithIgnoreCase(filename, SQL_FILE_SUFFIX)) {
                filename = filename + ".sql";
            }
            Files.makeDirs(directory);
            File file = new File(directory, filename);
            Files.makeFile(file);
            bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            final BufferedWriter bufferedWriter = bf;

            CommonTableGenerator generator = new CommonTableGenerator(databaseDescription);

            StringBuilder builder = new StringBuilder();
            Collects.forEach(ts, new Consumer<Table>() {
                @Override
                public void accept(Table t) {
                    try {
                        String ddl = generator.generate(t);
                        builder.append(ddl);
                        builder.append(LineDelimiter.DEFAULT.getValue());
                        IOs.write(ddl.getBytes(Charsets.UTF_8), bufferedWriter, Charsets.UTF_8);
                        bufferedWriter.write(LineDelimiter.DEFAULT.getValue());
                        bufferedWriter.flush();
                    } catch (Throwable ex) {
                        logger.error(ex.getMessage());
                    }

                }
            });
            if (postback) {
                return builder.toString();
            }
            return null;
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        } finally {
            IOs.close(bf);
            IOs.close(connection);
        }
    }
}