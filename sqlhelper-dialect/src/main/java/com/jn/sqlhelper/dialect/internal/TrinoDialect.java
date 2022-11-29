package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

/**
 *
 * select 语法：
 * <pre>
 * [ WITH [ RECURSIVE ] with_query [, ...] ]
 * SELECT [ ALL | DISTINCT ] select_expression [, ...]
 * [ FROM from_item [, ...] ]
 * [ WHERE condition ]
 * [ GROUP BY [ ALL | DISTINCT ] grouping_element [, ...] ]
 * [ HAVING condition]
 * [ WINDOW window_definition_list]
 * [ { UNION | INTERSECT | EXCEPT } [ ALL | DISTINCT ] select ]
 * [ ORDER BY expression [ ASC | DESC ] [, ...] ]
 * [ OFFSET count [ ROW | ROWS ] ]
 * [ LIMIT { count | ALL } ]
 * [ FETCH { FIRST | NEXT } [ count ] { ROW | ROWS } { ONLY | WITH TIES } ]
 *
 * </pre>
 *
 * 官网：
 * https://trino.io/docs/current/client/jdbc.html
 *
 * https://trino.io/docs/current/sql.html
 *
 */
public class TrinoDialect extends AbstractDialect {

    public TrinoDialect() {
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler().setSupportUsingIndexClauseInSelectEnd(true));
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return false;
    }

}
