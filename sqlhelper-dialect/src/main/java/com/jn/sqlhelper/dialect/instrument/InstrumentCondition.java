package com.jn.sqlhelper.dialect.instrument;

import com.jn.langx.util.Objects;
import com.jn.sqlhelper.common.utils.HashCodeBuilder;
import com.jn.sqlhelper.dialect.likeescaper.LikeEscaper;
import com.jn.sqlhelper.dialect.orderby.OrderBy;

import java.io.Serializable;

public class InstrumentCondition implements Serializable {

    /**
     * case null: not a pagination request
     * case true: a pagination request with a limit, offset
     * case false: a pagination request with a limit only
     */
    private Boolean limitOffset = null;

    private OrderBy orderBy;

    private String dialect;

    private boolean likeEscaped = false;

    private LikeEscaper likeEscaper;

    private boolean isCount = false;

    private String tenantColumn;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InstrumentCondition that = (InstrumentCondition) o;

        if (likeEscaped != that.likeEscaped) {
            return false;
        }
        if (isCount != that.isCount) {
            return false;
        }
        if (!Objects.equals(limitOffset, that.limitOffset)) {
            return false;
        }
        if (!Objects.equals(dialect, that.dialect)) {
            return false;
        }
        if (!Objects.equals(orderBy, that.orderBy)) {
            return false;
        }
        if (!Objects.equals(likeEscaper, that.likeEscaper)) {
            return false;
        }
        return Objects.equals(tenantColumn, that.tenantColumn);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .with(this.isCount)
                .with(this.likeEscaped)
                .with(this.likeEscaper)
                .with(this.limitOffset)
                .with(this.orderBy)
                .with(this.dialect)
                .with(this.tenantColumn)
                .build();
    }

    public Boolean getLimitOffset() {
        return limitOffset;
    }

    public void setLimitOffset(Boolean limitOffset) {
        this.limitOffset = limitOffset;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public boolean isLikeEscaped() {
        return likeEscaped;
    }

    public void setLikeEscaped(boolean likeEscaped) {
        this.likeEscaped = likeEscaped;
    }

    public LikeEscaper getLikeEscaper() {
        return likeEscaper;
    }

    public void setLikeEscaper(LikeEscaper likeEscaper) {
        this.likeEscaper = likeEscaper;
    }

    public boolean isCount() {
        return isCount;
    }

    public void setCount(boolean count) {
        isCount = count;
    }

    public String getTenantColumn() {
        return tenantColumn;
    }

    public void setTenantColumn(String tenantColumn) {
        this.tenantColumn = tenantColumn;
    }
}
