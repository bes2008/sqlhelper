package com.fjn.helper.sql.mybatis.plugins.pagination;

import com.google.gson.GsonBuilder;

public class PaginationPluginConfig {
    private boolean count = true;
    private int countCacheInitCapacity = 10;
    private int countCacheMaxCapacity = 1000;
    private String countSuffix = "_COUNT";
    private int countCacheExpireInSeconds = 5;
    private String dialect;
    private String dialectClassName;

    public PaginationPluginConfig() {
    }

    public boolean enableCountCache() {
        return this.countCacheMaxCapacity > 0;
    }

    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
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

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getDialectClassName() {
        return dialectClassName;
    }

    public void setDialectClassName(String dialectClassName) {
        this.dialectClassName = dialectClassName;
    }
}
