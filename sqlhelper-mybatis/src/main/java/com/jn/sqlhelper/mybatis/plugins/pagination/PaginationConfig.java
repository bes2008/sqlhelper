package com.jn.sqlhelper.mybatis.plugins.pagination;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.sqlhelper.dialect.pagination.PaginationProperties;

public class PaginationConfig extends PaginationProperties {
    private int countCacheInitCapacity = 10;
    private int countCacheMaxCapacity = 1000;
    private String countSuffix = "_COUNT";
    private int countCacheExpireInSeconds = 5;

    public boolean enableCountCache() {
        return this.countCacheMaxCapacity > 0;
    }

    @Override
    public String toString() {
        return JSONBuilderProvider.create().serializeNulls(true).build().toJson(this);
    }

    public int getCountCacheInitCapacity() {
        return countCacheInitCapacity;
    }

    public void setCountCacheInitCapacity(int countCacheInitCapacity) {
        this.countCacheInitCapacity = countCacheInitCapacity;
    }

    public int getCountCacheMaxCapacity() {
        return countCacheMaxCapacity;
    }

    public void setCountCacheMaxCapacity(int countCacheMaxCapacity) {
        this.countCacheMaxCapacity = countCacheMaxCapacity;
    }

    public String getCountSuffix() {
        return countSuffix;
    }

    public void setCountSuffix(String countSuffix) {
        this.countSuffix = countSuffix;
    }

    public int getCountCacheExpireInSeconds() {
        return countCacheExpireInSeconds;
    }

    public void setCountCacheExpireInSeconds(int countCacheExpireInSeconds) {
        this.countCacheExpireInSeconds = countCacheExpireInSeconds;
    }

}
