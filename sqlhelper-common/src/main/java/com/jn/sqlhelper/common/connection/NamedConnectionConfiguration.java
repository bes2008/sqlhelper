package com.jn.sqlhelper.common.connection;

public class NamedConnectionConfiguration extends ConnectionConfiguration {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
