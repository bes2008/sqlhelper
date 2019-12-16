package com.jn.sqlhelper.dialect;

import java.io.Serializable;

public class SqlRequest<R extends SqlRequest> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dialect;
    private boolean needLikeEscape;
    private LikeEscaper likeEscaper;

    public String getDialect() {
        return dialect;
    }

    public SqlRequest<R> setDialect(String dialect) {
        this.dialect = dialect;
        return this;
    }


    public boolean isNeedLikeEscape() {
        return needLikeEscape;
    }

    public SqlRequest<R> setNeedLikeEscape(boolean needLikeEscape) {
        this.needLikeEscape = needLikeEscape;
        return this;
    }

    public LikeEscaper getLikeEscaper() {
        return likeEscaper;
    }

    public SqlRequest<R> setLikeEscaper(LikeEscaper likeEscaper) {
        this.likeEscaper = likeEscaper;
        return this;
    }

}
