package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

/**
 * http://hawq.apache.org/docs/userguide/2.3.0.0-incubating/reference/sql/SELECT.html
 * <p>
 * SELECT [ALL | DISTINCT [ON (<expression> [, ...])]]
 * | <expression> [[AS] <output_name>] [, ...]
 * [FROM <from_item> [, ...]]
 * [WHERE <condition>]
 * [GROUP BY <grouping_element> [, ...]]
 * [HAVING <condition> [, ...]]
 * [WINDOW <window_name> AS (<window_specification>)]
 * [{UNION | INTERSECT | EXCEPT} [ALL] <select>]
 * [ORDER BY <expression> [ASC | DESC | USING <operator>] [, ...]]
 * [LIMIT {<count> | ALL}]
 * [OFFSET <start>]
 */
public class HawqDialect extends AbstractDialect {
    public HawqDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
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
    public boolean isBindLimitParametersInReverseOrder() {
        return true;
    }
}
