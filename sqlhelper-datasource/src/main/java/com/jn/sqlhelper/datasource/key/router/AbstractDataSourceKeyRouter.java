package com.jn.sqlhelper.datasource.key.router;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;

import java.util.List;

public abstract class AbstractDataSourceKeyRouter implements DataSourceKeyRouter {
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
