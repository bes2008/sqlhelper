package com.jn.sqlhelper.datasource.key.router;

import com.jn.langx.Named;
import com.jn.langx.Ordered;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Function2;
import com.jn.sqlhelper.datasource.key.DataSourceKey;

import java.util.List;

public abstract class DataSourceKeyRouter implements Function2<List<DataSourceKey>, MethodInvocation, DataSourceKey>, Named, Ordered {
    private String name = "undefined";
    private int order;
    private List<String> groups = Collects.emptyArrayList();

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

    /**
     * 如果没有合适的，返回 null 即可
     */
    @Override
    public abstract DataSourceKey apply(@NonNull List<DataSourceKey> dataSourceKeys, @Nullable MethodInvocation methodInvocation);

    /**
     * 可应用与哪些 group
     */
    @NonNull
    public List<String> getApplyGroups() {
        return groups;
    }

    public void setApplyGroups(List<String> groups) {
        if (Emptys.isNotEmpty(groups)) {
            this.groups = groups;
        }
    }
}
