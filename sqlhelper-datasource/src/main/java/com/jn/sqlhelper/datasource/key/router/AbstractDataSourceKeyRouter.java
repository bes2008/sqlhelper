package com.jn.sqlhelper.datasource.key.router;

public abstract class AbstractDataSourceKeyRouter implements DataSourceKeyRouter {
    private String name = "undefined";

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }


}
