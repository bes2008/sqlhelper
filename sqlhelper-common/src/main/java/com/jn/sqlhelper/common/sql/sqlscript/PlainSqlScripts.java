package com.jn.sqlhelper.common.sql.sqlscript;

import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.spi.CommonServiceProvider;
import com.jn.sqlhelper.common.jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class PlainSqlScripts {
    private static final Logger logger = LoggerFactory.getLogger(PlainSqlScripts.class);

    static {
        parserRegistry = new GenericRegistry<>();
        Pipeline.of(CommonServiceProvider.loadService(PlainSqlScriptParser.class))
                .forEach(new Consumer<PlainSqlScriptParser>() {
                    @Override
                    public void accept(PlainSqlScriptParser plainSqlScriptParser) {
                        parserRegistry.register(plainSqlScriptParser);
                    }
                });
    }

    public final static GenericRegistry<PlainSqlScriptParser> parserRegistry;
    public static void execute(JdbcTemplate sqlHelperJdbcTemplate, PlainSqlScript sqlScript, PlainSqlScriptParser parser){
        execute(sqlHelperJdbcTemplate, sqlScript, parser, null);
    }

    public static void execute(JdbcTemplate sqlHelperJdbcTemplate, PlainSqlScript sqlScript, PlainSqlScriptParser parser, Predicate<String> excludePredicate) {
        List<PlainSqlStatement> sqlStatements = parser.parse(sqlScript);
        for (PlainSqlStatement sqlStatement : sqlStatements) {
            String sql = sqlStatement.getSql();
            if (excludePredicate != null) {
                if (excludePredicate.test(sql)) {
                    if(logger.isDebugEnabled()) {
                        logger.debug("Excluded SQL: \n{}", sql);
                    }
                    continue;
                }
            }
            if(logger.isDebugEnabled()) {
                logger.debug("Executing SQL: \n{}", sql);
            }
            try {
                sqlHelperJdbcTemplate.execute(sql);
            } catch (SQLException e) {
                throw new PlainSqlScriptException(sqlScript.getResource(), sqlStatement, e);
            }
        }
    }
}
