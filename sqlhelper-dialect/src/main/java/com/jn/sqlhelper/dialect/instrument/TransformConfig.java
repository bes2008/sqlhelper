package com.jn.sqlhelper.dialect.instrument;

import com.jn.langx.util.Objs;
import com.jn.langx.util.hash.HashCodeBuilder;
import com.jn.sqlhelper.dialect.instrument.groupby.GroupByTransformConfig;
import com.jn.sqlhelper.dialect.instrument.where.WhereTransformConfig;
import com.jn.sqlhelper.dialect.likeescaper.LikeEscaper;
import com.jn.sqlhelper.dialect.orderby.OrderBy;
import com.jn.sqlhelper.dialect.tenant.Tenant;

import java.io.Serializable;
import java.util.List;

/**
 * 每一次进行转换操作，都需要有一个 TransformConfig
 */
public class TransformConfig implements Serializable {

    /**
     * 分页参数
     *
     * case null: not a pagination request
     * case true: a pagination request with a limit, offset
     * case false: a pagination request with a limit only
     */
    private Boolean limitOffset = null;

    /**
     * 数据库方言
     */
    private String dialect;

    /**
     * 是否进行 like 转义操作
     */
    private boolean likeEscaped = false;

    /**
     * 自定义的like 转义器
     */
    private LikeEscaper likeEscaper;

    /**
     * 是否 为 select count 请求
     */
    private boolean isCount = false;

    private boolean isMultiTenant = false;

    /**
     * 对 where 条件的转换配置
     */
    private List<WhereTransformConfig> whereInstrumentConfigs;

    /**
     * order by 子句的转换配置
     */
    private OrderBy orderBy;

    /**
     * group by 子句的转换配置
     */
    private List<GroupByTransformConfig> groupByTransformConfigs;

    private Tenant tenant;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TransformConfig that = (TransformConfig) o;

        if (likeEscaped != that.likeEscaped) {
            return false;
        }
        if (isCount != that.isCount) {
            return false;
        }
        if (isMultiTenant != that.isMultiTenant) {
            return false;
        }
        if (!Objs.equals(limitOffset, that.limitOffset)) {
            return false;
        }
        if (!Objs.equals(dialect, that.dialect)) {
            return false;
        }
        if (!Objs.equals(orderBy, that.orderBy)) {
            return false;
        }
        if (!Objs.equals(likeEscaper, that.likeEscaper)) {
            return false;
        }

        if (!Objs.equals(whereInstrumentConfigs, that.whereInstrumentConfigs)) {
            return false;
        }
        if (!Objs.equals(tenant, that.tenant)) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .with(this.isCount)
                .with(this.isMultiTenant)
                .with(this.likeEscaped)
                .with(this.likeEscaper)
                .with(this.limitOffset)
                .with(this.orderBy)
                .with(this.dialect)
                .with(this.whereInstrumentConfigs)
                .with(this.tenant)
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

    public List<WhereTransformConfig> getWhereInstrumentConfigs() {
        return whereInstrumentConfigs;
    }

    public void setWhereInstrumentConfigs(List<WhereTransformConfig> configs) {
        this.whereInstrumentConfigs = configs;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
