package com.jn.sqlhelper.dialect;

import java.io.Serializable;

public class SqlRequest<R extends SqlRequest, C extends SqlRequestContext<R>> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dialect;
    private boolean escapeLikeParameter;
    private LikeEscaper likeEscaper;
    private C context;



    public String getDialect() {
        return dialect;
    }

    public SqlRequest<R,C> setDialect(String dialect) {
        this.dialect = dialect;
        return this;
    }

    public boolean isEscapeLikeParameter() {
        return escapeLikeParameter;
    }

    public SqlRequest<R,C> setEscapeLikeParameter(boolean escapeLikeParameter) {
        this.escapeLikeParameter = escapeLikeParameter;
        return this;
    }

    public LikeEscaper getLikeEscaper() {
        return likeEscaper;
    }

    public SqlRequest<R,C> setLikeEscaper(LikeEscaper likeEscaper) {
        this.likeEscaper = likeEscaper;
        return this;
    }

    public C getContext() {
        return context;
    }

    public SqlRequest<R,C> setContext(C context) {
        this.context = context;
        return this;
    }

    public boolean isPagingRequest(){
        return false;
    }
}
