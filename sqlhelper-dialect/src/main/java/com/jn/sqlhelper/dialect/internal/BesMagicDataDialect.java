package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.sqlhelper.common.ddl.SQLSyntaxCompatTable;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatementBuilder;
import com.jn.sqlhelper.dialect.annotation.Driver;
import com.jn.sqlhelper.dialect.annotation.SyntaxCompat;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;
import com.jn.sqlhelper.dialect.internal.sqlscript.PostgreSQLSqlStatementBuilder;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;

/**
 * 基于OpenGauss改造
 *
 *
 * <pre>
 *     select 语法：
 *
 *     [ WITH [ RECURSIVE ] with_query [, ...] ]
 * SELECT [/*+ plan_hint *\/][ALL|DISTINCT[ON(expression[,...])]]
        *{*|{expression[[AS]output_name]}[,...]}
        *[FROM from_item[,...]]
        *[WHERE condition]
        *[GROUP BY grouping_element[,...]]
        *[HAVING condition[,...]]
        *[WINDOW{window_name AS(window_definition)}[,...]]
        *[{UNION|INTERSECT|EXCEPT|MINUS}[ALL|DISTINCT]select]
        *[ORDER BY{expression[[ASC|DESC|USING operator]|nlssort_expression_clause][NULLS{FIRST|LAST}]}[,...]]
        *[LIMIT{[offset,]count|ALL}]
        *[OFFSET start[ROW|ROWS]]
        *[FETCH{FIRST|NEXT}[count]{ROW|ROWS}ONLY]
        *[{FOR{UPDATE|SHARE}[OF table_name[,...]][NOWAIT]}[...]];
 * </pre>
 */
@Name("besmagicdata")
@SyntaxCompat(SQLSyntaxCompatTable.POSTGRESQL)
@Driver("com.bes.magicdata.Driver")
public class BesMagicDataDialect extends AbstractDialect {
    public BesMagicDataDialect() {
        super();
        setLimitHandler(new LimitOffsetLimitHandler());
        setLikeEscaper(BackslashStyleEscaper.NON_DEFAULT_INSTANCE);
        setPlainSqlScriptParser(new BesMagicDataSQLScriptParser());
    }

    @Override
    public IdentifierCase unquotedIdentifierCase() {
        return IdentifierCase.LOWER_CASE;
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

    private static class BesMagicDataSQLScriptParser extends PlainSqlScriptParser {
        @Override
        protected PlainSqlStatementBuilder newSqlStatementBuilder() {
            return new PostgreSQLSqlStatementBuilder();
        }
    }


}
