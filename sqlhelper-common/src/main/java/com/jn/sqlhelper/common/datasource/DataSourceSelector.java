package com.jn.agileway.jdbc.datasource;

public abstract class DataSourceSelector {
    protected GroupedDataSourceRegistry registry;

    public void setDataSourceRegistry(GroupedDataSourceRegistry registry) {
        this.registry = registry;
    }

    public abstract void select();
}
