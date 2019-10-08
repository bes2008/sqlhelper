package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

/**
 * <pre>
 * SELECT [ ALL | DISTINCT ] select_list [AS other_name]
 * FROM table_name
 * [WHERE where_conditions ]
 * [GROUP BY group_by_list ]
 * [HAVING search_confitions ]
 * [ORDER BY order_list [ASC | DESC ] ]
 * [LIMIT {[offset,] row_count | row_count OFFSET offset}];
 * </pre>
 */
public class OBaseDialect extends AbstractDialect {

    public OBaseDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

}
