package com.fjn.helper.sql.dialect;

public final class RowSelection {
    private Integer offset;
    private Integer limit;
    private Integer timeout;
    private Integer fetchSize;

    public Integer getOffset() {
        return this.offset;
    }

    public void setOffset(final Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public void setLimit(final Integer limit) {
        this.limit = limit;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public void setTimeout(final Integer timeout) {
        if (timeout != null && timeout >= 0) {
            this.timeout = timeout;
        }
    }

    public Integer getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(final Integer fetchSize) {
        if (fetchSize != null && fetchSize > 0) {
            this.fetchSize = fetchSize;
        }
    }

    public boolean definesLimits() {
        return this.limit != null || (this.offset != null && this.offset <= 0);
    }
}
