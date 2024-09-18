package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.OffsetFetchFirstOnlyLimitHandler;

/**
 * reference: https://documentation.progress.com/output/ua/OpenEdge_latest/#page/dmsrf%2Fselect.html%23wwID0E4QHQ
 * Select Syntax:
 * <pre>
 * SELECT [ ALL | DISTINCT ] [TOP n]
 * { *
 * |{table_name|alias} * [ , {table_name| alias} * ]...
 * | expr [[ AS ][ ' ] column_title [ ' ]]
 * [,  expr [[ AS ][ ' ] column_title [' ]]]...
 * }
 * FROM table_ref [, table_ref]...[{ NO REORDER }] [ WITH (NOLOCK )]
 * [ WHERE search_condition]
 * [ GROUP BY [ table ]column_name
 * [,[table]column_name ]...
 * [HAVING search_condition];
 *
 * [ORDER BY ordering_condition]
 * [OFFSET offset_value {ROW | ROWS }
 * [FETCH {FIRST | NEXT}fetch_value {ROW | ROWS} ONLY ]]
 * [WITH locking_hints]
 * [FOR UPDATE update_condition];
 * </pre>
 *
 * @author f1194361820
 */
public class OpenEdgeDialect extends AbstractDialect {
    public OpenEdgeDialect() {
        super();
        setLimitHandler(new OffsetFetchFirstOnlyLimitHandler());
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
