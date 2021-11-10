package com.jn.sqlhelper.common.sql.sqlscript;

import com.jn.langx.Parser;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PlainSqlScriptParser implements Parser<PlainSqlScript, List<PlainSqlStatement>> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final PlainSqlScriptParser INSTANCE = new PlainSqlScriptParser();
    @Override
    public List<PlainSqlStatement> parse(PlainSqlScript sqlScript) {
        Resource sqlScriptResource = sqlScript.getResource();
        final List<String> lines = Collects.emptyArrayList();
        Resources.readUsingDelimiter(sqlScriptResource, "\n", Charsets.getCharset(sqlScript.getEncoding()), new Consumer<String>() {
            @Override
            public void accept(String line) {
                lines.add(line);
            }
        });
        List<PlainSqlStatement> sqls = linesToStatements(lines);
        return sqls;
    }

    protected PlainSqlStatementBuilder newSqlStatementBuilder(){
       return new PlainSqlStatementBuilder();
    }

    /**
     * Turns these lines in a series of statements.
     *
     * @param lines The lines to analyse.
     * @return The statements contained in these lines (in order).
     */
    /* private -> for testing */
    protected List<PlainSqlStatement> linesToStatements(List<String> lines) {
        List<PlainSqlStatement> statements = new ArrayList<PlainSqlStatement>();

        PlainSqlDelimiter nonStandardDelimiter = null;
        PlainSqlStatementBuilder sqlStatementExtractor = newSqlStatementBuilder();

        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);

            if (sqlStatementExtractor.isEmpty()) {
                if (Strings.isBlank(line)) {
                    // Skip empty line between statements.
                    continue;
                }

                PlainSqlDelimiter newDelimiter = sqlStatementExtractor.extractNewDelimiterFromLine(line);
                if (newDelimiter != null) {
                    nonStandardDelimiter = newDelimiter;
                    // Skip this line as it was an explicit delimiter change directive outside of any statements.
                    continue;
                }

                sqlStatementExtractor.setLineNumber(lineNumber);

                // Start a new statement, marking it with this line number.
                if (nonStandardDelimiter != null) {
                    sqlStatementExtractor.setDelimiter(nonStandardDelimiter);
                }
            }

            sqlStatementExtractor.addLine(line);

            if (sqlStatementExtractor.isTerminated()) {
                PlainSqlStatement sqlStatement = sqlStatementExtractor.getSqlStatement();
                statements.add(sqlStatement);
                logger.debug("Found statement at line " + sqlStatement.getLineNumber() + ": " + sqlStatement.getSql());

                sqlStatementExtractor = newSqlStatementBuilder();
            } else if (sqlStatementExtractor.canDiscard()) {
                sqlStatementExtractor = newSqlStatementBuilder();
            }
        }

        // Catch any statements not followed by delimiter.
        if (!sqlStatementExtractor.isEmpty()) {
            statements.add(sqlStatementExtractor.getSqlStatement());
        }

        return statements;
    }
}
