package com.jn.sqlhelper.cli.commands;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.io.file.Files;
import com.jn.sqlhelper.common.connection.ConnectionFactory;
import com.jn.sqlhelper.common.connection.NamedConnectionConfiguration;
import com.jn.sqlhelper.common.ddl.dump.DatabaseLoader;
import com.jn.sqlhelper.common.ddl.model.DatabaseDescription;
import com.jn.sqlhelper.common.ddl.model.Index;
import com.jn.sqlhelper.common.ddl.model.Table;
import com.jn.sqlhelper.dialect.ddl.generator.CommonTableGenerator;
import com.jn.sqlhelper.langx.configuration.file.directoryfile.DirectoryBasedFileConfigurationRepository;
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

@ShellComponent
public class ShowDatabaseCommands {

    @Autowired
    DirectoryBasedFileConfigurationRepository<NamedConnectionConfiguration> repository;

    private Connection getConnectionByConnectionConfigurationId(String id) {
        NamedConnectionConfiguration configuration = repository.getById(id);
        Preconditions.checkNotNull(configuration, StringTemplates.formatWithPlaceholder("Can't find a connection configuration named {}", id));
        ConnectionFactory factory = new ConnectionFactory(configuration);
        return factory.getConnection();
    }

    @ShellMethod(key = "show tables", value = "Show table names")
    public List<String> getTableNames(@ShellOption(help = "the connection configuration name") String connectionName) {
        Connection connection = getConnectionByConnectionConfigurationId(connectionName);

        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            List<Table> tables = new DatabaseLoader().loadTables(new DatabaseDescription(dbMetaData), "TEST", "PUBLIC", null);
            return Pipeline.of(tables).map(new Function<Table, String>() {
                @Override
                public String apply(Table table) {
                    return table.getName();
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
            return new DatabaseLoader().loadTable(new DatabaseDescription(dbMetaData), "TEST", "PUBLIC", table);
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
            List<Index> indexes = new DatabaseLoader().findTableIndexes(new DatabaseDescription(dbMetaData), "TEST", "PUBLIC", table);
            return Pipeline.of(indexes).map(new Function<Index, String>() {
                @Override
                public String apply(Index index) {
                    return index.getName();
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
            Table t = new DatabaseLoader().loadTable(new DatabaseDescription(dbMetaData), "TEST", "PUBLIC", null);
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
            Table t = new DatabaseLoader().loadTable(databaseDescription, "TEST", "PUBLIC", table);
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
    public String getTableDDL(@ShellOption(help = "the connection configuration name") String connectionName,
                              @ShellOption(help = "the table name") String table,
                              @ShellOption(help = "the dump directory") String directory,
                              @ShellOption(help = "the dump filename") String filename) {
        Connection connection = getConnectionByConnectionConfigurationId(connectionName);
        BufferedWriter bf = null;
        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            DatabaseDescription databaseDescription = new DatabaseDescription(dbMetaData);
            Table t = new DatabaseLoader().loadTable(databaseDescription, "TEST", "PUBLIC", null);
            Preconditions.checkNotNull(t, StringTemplates.formatWithPlaceholder("table {} is not exists", table));

            if (!Strings.endsWithIgnoreCase(filename,"sql")) {
                filename = filename + ".sql";
            }
            Files.makeDirs(directory);
            File file = new File(directory, filename);
            Files.makeFile(file);
            bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));


            CommonTableGenerator generator = new CommonTableGenerator(databaseDescription);
            String ddl = generator.generate(t);
            IOs.write(ddl.getBytes(Charsets.UTF_8), bf, Charsets.UTF_8);
            bf.flush();
            return ddl;
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        } finally {
            IOs.close(bf);
            IOs.close(connection);
        }
    }
}