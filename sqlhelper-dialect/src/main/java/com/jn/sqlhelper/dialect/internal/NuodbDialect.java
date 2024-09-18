package com.jn.sqlhelper.dialect.internal;

import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;

/**
 * http://doc.nuodb.com/Latest/Default.htm#SELECT.htm
 * <pre>
 *     SELECT  [ optimizer_hint ]
 * [ ALL | DISTINCT ]
 * { select_item [ [AS] output_name ] } [, ...]
 * FROM from_item
 * [ WHERE condition ]
 * [ GROUP BY expression [, ...] [ HAVING condition [, ...] ] ]
 * [ UNION [ ALL | DISTINCT ] select ]
 * [ ORDER BY { order_list [ COLLATE collation_name ] [ ASC | DESC] } [, ...] ]
 * [ LIMIT { count [ OFFSET start ] | start [ , count ] }
 * [ OFFSET start [ ROW | ROWS ] [ FETCH {FIRST | NEXT}
 * count [ROW | ROWS] [ONLY] ] ]
 * [ FETCH {FIRST | NEXT } count [ROW | ROWS] [ONLY] ]
 * [ FOR UPDATE [NOWAIT] ]
 * </pre>
 * <p>
 * <p>
 * supports:
 * 1) LIMIT count
 * 2) LIMIT start, count
 * 3) LIMIT count OFFSET START
 * <p>
 * we use LIMIT start, count
 */
public class NuodbDialect extends AbstractDialect {
    public NuodbDialect() {
        super();
        setLimitHandler(new LimitCommaLimitHandler());
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return true;
    }

    @Override
    public boolean isSupportsLimit() {
        return true;
    }
}
