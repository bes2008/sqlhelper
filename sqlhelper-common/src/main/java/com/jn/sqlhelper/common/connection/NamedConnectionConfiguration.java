package com.jn.sqlhelper.common.connection;

import com.jn.sqlhelper.langx.configuration.Configuration;

public class NamedConnectionConfiguration extends ConnectionConfiguration implements Configuration {
    private String name;

    public NamedConnectionConfiguration() {

    }

    public NamedConnectionConfiguration(ConnectionConfiguration configuration) {
        setDriver(configuration.getDriver());
        setUser(configuration.getUser());
        setPassword(configuration.getPassword());
        setUrl(configuration.getUrl());
        setDriverProps(configuration.getDriverProps());
    }

    @Override
    public void setId(String id) {
        name = id;
    }

    @Override
    public String getId() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
