package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.dialect.annotation.Driver;
import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.pagination.RowSelection;
import com.jn.sqlhelper.dialect.internal.limit.AbstractLimitHandler;

/**
 * select *
 * from table
 * where xxx
 * order by xxx
 * limit (start, end)
 *
 * start based on 1
 *
 * 东方国信  CirroData
 * 参考文档：http://www.cirrodata.com/support/wdcl/quanbu/index.html
 *
 *
 * URL syntax:
 * <pre>
 *      行云驱动：jdbc:xcloud:@<host_ip>:<host_port>/<dbName>
 *      存储过程驱动：com.bonc.xcloud.sp.jdbc.XCloudSPDriver
 * </pre>
 *
 */
@Driver({
        "com.bonc.xcloud.jdbc.XCloudDriver",
        "com.bonc.xcloud.sp.jdbc.XCloudSPDriver"
})
@Name("xcloud")
public class XCloudDBDialect extends AbstractDialect {
    public XCloudDBDialect() {
        super();
        LimitCommaLimitHandler limitHandler = new LimitCommaLimitHandler().setWithBrace(true);
        limitHandler.setOffsetBased(1);
        setLimitHandler(limitHandler);
        setLikeEscaper(BackslashStyleEscaper.INSTANCE);
    }

    @Override
    public boolean isUseMaxForLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return true;
    }
}
