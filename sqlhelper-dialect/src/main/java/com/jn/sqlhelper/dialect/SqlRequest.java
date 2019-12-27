package com.jn.sqlhelper.dialect;

import com.jn.easyjson.core.annotation.Ignore;

import java.io.Serializable;

public class SqlRequest<R extends SqlRequest, C extends SqlRequestContext<R>> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * the dialect, every Dialect is also a likeEscaper;
     */
    private String dialect;
    /**
     * the customer likeEscaper
     */
    private LikeEscaper likeEscaper;

    private Boolean escapeLikeParameter; // will be used for prepared statement
    @Ignore
    private C context;


    private Integer fetchSize;
    private int maxRows = -1;

    public String getDialect() {
        return dialect;
    }

    public SqlRequest<R, C> setDialect(String dialect) {
        this.dialect = dialect;
        return this;
    }

    public Boolean isEscapeLikeParameter() {
        return escapeLikeParameter;
    }

    public SqlRequest<R, C> setEscapeLikeParameter(Boolean escapeLikeParameter) {
        this.escapeLikeParameter = escapeLikeParameter;
        return this;
    }

    public LikeEscaper getLikeEscaper() {
        return likeEscaper;
    }

    public SqlRequest<R, C> setLikeEscaper(LikeEscaper likeEscaper) {
        this.likeEscaper = likeEscaper;
        return this;
    }

    public C getContext() {
        return context;
    }

    public SqlRequest<R, C> setContext(C context) {
        this.context = context;
        return this;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public Integer getFetchSize() {
        return this.fetchSize;
    }

    public SqlRequest<R, C> setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    public boolean isPagingRequest() {
        return false;
    }
}
