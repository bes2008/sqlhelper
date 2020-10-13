package com.jn.sqlhelper.mybatis.plugins.pagination;

import com.jn.easyjson.core.JSONBuilderProvider;
import com.jn.sqlhelper.dialect.pagination.PaginationProperties;

public class PaginationConfig extends PaginationProperties {
    /**
     * count sql 缓存的初始容量
     */
    private int countCacheInitCapacity = 10;
    /**
     * count sql 缓存的最大容量
     */
    private int countCacheMaxCapacity = 1000;
    /**
     * count sql 的后缀
     */
    private String countSuffix = "_COUNT";
    /**
     * count sql 在cache中存活时间
     */
    private int countCacheExpireInSeconds = 5;

    public boolean enableCountCache() {
        return this.countCacheMaxCapacity > 0;
    }

    private boolean pageHelperCompatible = true;
    private String pageHelperHandlerClass = "com.github.pagehelper.PageHelperHandler";

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

    public boolean isPageHelperCompatible() {
        return pageHelperCompatible;
    }

    public void setPageHelperCompatible(boolean pageHelperCompatible) {
        this.pageHelperCompatible = pageHelperCompatible;
    }

    public String getPageHelperHandlerClass() {
        return pageHelperHandlerClass;
    }

    public void setPageHelperHandlerClass(String pageHelperHandlerClass) {
        this.pageHelperHandlerClass = pageHelperHandlerClass;
    }
}
