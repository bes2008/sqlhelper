package com.jn.sqlhelper.dialect;

import com.jn.easyjson.core.annotation.Ignore;

import java.io.Serializable;
import java.util.List;

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

    private List<Integer> likeParameterIndexes = null;


    private Boolean escapeLikeParameter; // will be used for prepared statement
    @Ignore
    private C context;


    private Integer fetchSize;
    private int maxRows = -1;

    public String getDialect() {
        return dialect;
    }

    public R setDialect(String dialect) {
        this.dialect = dialect;
        return (R)this;
    }

    public Boolean isEscapeLikeParameter() {
        return escapeLikeParameter;
    }

    public R setEscapeLikeParameter(Boolean escapeLikeParameter) {
        this.escapeLikeParameter = escapeLikeParameter;
        return (R)this;
    }

    public LikeEscaper getLikeEscaper() {
        return likeEscaper;
    }

    public R setLikeEscaper(LikeEscaper likeEscaper) {
        this.likeEscaper = likeEscaper;
        return (R)this;
    }

    public C getContext() {
        return context;
    }

    public R setContext(C context) {
        this.context = context;
        return (R)this;
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

    public R setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return (R)this;
    }

    public boolean isPagingRequest() {
        return false;
    }

    public List<Integer> getLikeParameterIndexes() {
        return likeParameterIndexes;
    }

    public void setLikeParameterIndexes(List<Integer> likeParameterIndexes) {
        this.likeParameterIndexes = likeParameterIndexes;
    }

    public void clear(){
        likeEscaper = null;
        context = null;
    }
}
