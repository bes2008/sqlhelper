package com.jn.sqlhelper.dialect;

public class SelectRequest<R extends SelectRequest> extends SqlRequest<R> {
    private boolean needLikeEscape;
    private LikeEscaper likeEscaper;

    public boolean isNeedLikeEscape() {
        return needLikeEscape;
    }

    public SelectRequest<R> setNeedLikeEscape(boolean needLikeEscape) {
        this.needLikeEscape = needLikeEscape;
        return this;
    }

    public LikeEscaper getLikeEscaper() {
        return likeEscaper;
    }

    public SelectRequest<R> setLikeEscaper(LikeEscaper likeEscaper) {
        this.likeEscaper = likeEscaper;
        return this;
    }
}
