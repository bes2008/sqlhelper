package com.jn.sqlhelper.common.sql.sqlscript;

import com.jn.sqlhelper.common.jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class PlainSqlScripts {
    private static final Logger logger = LoggerFactory.getLogger(PlainSqlScripts.class);

    public static void execute(JdbcTemplate jdbcTemplate, PlainSqlScript sqlScript, PlainSqlScriptParser parser) {
        List<PlainSqlStatement> sqlStatements = parser.parse(sqlScript);

        for (PlainSqlStatement sqlStatement : sqlStatements) {
            String sql = sqlStatement.getSql();
            logger.debug("Executing SQL: " + sql);

            try {
                jdbcTemplate.execute(sql);
            } catch (SQLException e) {
                throw new PlainSqlScriptException(sqlScript.getResource(), sqlStatement, e);
            }
        }
    }
}
