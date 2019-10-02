package com.jn.sqlhelper.springjdbc.statement;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

    private final String sql;

    public SimplePreparedStatementCreator(String sql) {
        Preconditions.checkNotNull(sql, "SQL must not be null");
        this.sql = sql;
    }

    @NonNull
    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        return con.prepareStatement(this.sql);
    }

    @Override
    public String getSql() {
        return this.sql;
    }

}