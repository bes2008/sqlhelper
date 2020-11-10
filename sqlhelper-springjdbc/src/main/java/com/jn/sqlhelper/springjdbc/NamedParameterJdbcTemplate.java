package com.jn.sqlhelper.springjdbc;

import com.jn.langx.util.Preconditions;
import com.jn.sqlhelper.springjdbc.statement.NamedParameterPreparedStatementCreatorFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.List;

public class NamedParameterJdbcTemplate extends org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate {
    public NamedParameterJdbcTemplate(DataSource dataSource) {
        super(new JdbcTemplate(dataSource));
    }

    /**
     * Create a new NamedParameterJdbcTemplate for the given classic
     * Spring {@link com.jn.sqlhelper.springjdbc.JdbcTemplate}.
     *
     * @param classicJdbcTemplate the classic Spring JdbcTemplate to wrap
     */
    public NamedParameterJdbcTemplate(JdbcOperations classicJdbcTemplate) {
        super(classicJdbcTemplate);
    }

    /**
     * Expose the classic Spring {@link org.springframework.jdbc.core.JdbcTemplate} itself, if available,
     * in particular for passing it on to other {@code JdbcTemplate} consumers.
     * <p>If sufficient for the purposes at hand, {@link #getJdbcOperations()}
     * is recommended over this variant.
     *
     * @since 3.2.5
     */
    public org.springframework.jdbc.core.JdbcTemplate getJdbcTemplate() {
        JdbcOperations operations = getJdbcOperations();
        Preconditions.checkArgument(operations instanceof org.springframework.jdbc.core.JdbcTemplate, "No JdbcTemplate available");
        return (org.springframework.jdbc.core.JdbcTemplate) operations;
    }

    /**
     * Build a {@link PreparedStatementCreator} based on the given SQL and named parameters.
     * <p>Note: Directly called from all {@code query} variants.
     * Not used for the {@code update} variant with generated key handling.
     *
     * @param sql         the SQL statement to execute
     * @param paramSource container of arguments to bind
     * @return the corresponding {@link PreparedStatementCreator}
     */
    @Override
    protected PreparedStatementCreator getPreparedStatementCreator(String sql, SqlParameterSource paramSource) {
        ParsedSql parsedSql = getParsedSql(sql);
        PreparedStatementCreatorFactory pscf = getPreparedStatementCreatorFactory(parsedSql, paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
        return pscf.newPreparedStatementCreator(params);
    }

    /**
     * Build a {@link PreparedStatementCreatorFactory} based on the given SQL and named parameters.
     *
     * @param parsedSql   parsed representation of the given SQL statement
     * @param paramSource container of arguments to bind
     * @return the corresponding {@link PreparedStatementCreatorFactory}
     * @see #getParsedSql(String)
     * @since Spring 5.1.3
     */
    protected PreparedStatementCreatorFactory getPreparedStatementCreatorFactory(
            ParsedSql parsedSql, SqlParameterSource paramSource) {
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
        List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
        return new NamedParameterPreparedStatementCreatorFactory(sqlToUse, declaredParameters);
    }
}
