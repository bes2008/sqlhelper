package com.jn.sqlhelper.datasource.key.router;

import com.jn.langx.Named;
import com.jn.langx.Ordered;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Function2;
import com.jn.sqlhelper.datasource.DataSourceRegistry;
import com.jn.sqlhelper.datasource.DataSourceRegistryAware;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

import java.util.List;

public abstract class DataSourceKeyRouter implements Function2<List<DataSourceKey>, MethodInvocation, DataSourceKey>, Named, Ordered, DataSourceRegistryAware {
    private String name;
    private int order;
    protected DataSourceRegistry dataSourceRegistry;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void setDataSourceRegistry(DataSourceRegistry registry) {
        this.dataSourceRegistry = registry;
    }

    /**
     * 如果没有合适的，返回 null 即可
     *
     * @return
     */
    @Override
    public abstract DataSourceKey apply(@NonNull List<DataSourceKey> dataSourceKeys, @Nullable MethodInvocation methodInvocation);

    /**
     * 可应用与哪些 group
     */
    @NonNull
    public List<String> applyTo() {
        return Collects.emptyArrayList();
    }
}
