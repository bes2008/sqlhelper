package com.jn.sqlhelper.cli.commands;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.StringMap;
import com.jn.langx.util.function.Consumer2;
import com.jn.sqlhelper.common.connection.ConnectionConfiguration;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Properties;

@ShellComponent("connection")
public class JdbcConnectionCommands {
    @ShellMethod(key = "create")
    public ConnectionConfiguration addConnection(@NonNull String driver, @NonNull String url, @NonNull String username, @Nullable String password, @Nullable String props){
        Preconditions.checkNotNull(driver);
        Preconditions.checkNotNull(url);
        Preconditions.checkNotNull(username);

        StringMap propsMap = new StringMap(props, "=","&");
        final Properties properties = new Properties();
        Collects.forEach(propsMap, new Consumer2<String, String>() {
            @Override
            public void accept(String key, String value) {
                properties.setProperty(key, value);
            }
        });

        ConnectionConfiguration configuration = new ConnectionConfiguration(driver, url, username, password, properties);
        return configuration;

    }
}
