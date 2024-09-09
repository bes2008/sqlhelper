/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.ddl.dump.DatabaseLoader;
import com.jn.sqlhelper.common.ddl.dump.TableGenerator;
import com.jn.sqlhelper.common.ddl.model.DatabaseDescription;
import com.jn.sqlhelper.common.ddl.model.Table;
import com.jn.sqlhelper.common.exception.TableNonExistsException;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.utils.SQLs;
import com.jn.sqlhelper.dialect.*;
import com.jn.sqlhelper.dialect.ddl.generator.CommonTableGenerator;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.DefaultLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHandler;
import com.jn.sqlhelper.dialect.internal.urlparser.CommonUrlParser;
import com.jn.sqlhelper.dialect.internal.urlparser.NoopUrlParser;
import com.jn.sqlhelper.dialect.internal.urlparser.UrlParser;
import com.jn.sqlhelper.dialect.likeescaper.LikeEscaper;
import com.jn.sqlhelper.dialect.pagination.RowSelection;

import java.sql.CallableStatement;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;


public abstract class AbstractDialect<T extends AbstractDialect> implements Dialect {
    private AbstractDialect delegate = null;
    private UrlParser urlParser;
    private LimitHandler limitHandler;
    private LikeEscaper likeEscaper;
    // limit 的参数默认是否要使用 `?`，如果没有指定值，则根据数据库是否支持来进行计算
    private Boolean isUseLimitInVariableMode = null;
    private PlainSqlScriptParser plainSqlScriptParser;

    private final Properties properties = new Properties();


    public AbstractDialect() {
        setLimitHandler(new DefaultLimitHandler(this));
        setUrlParser(new NoopUrlParser());
        setLikeEscaper(BackslashStyleEscaper.INSTANCE);
        setPlainSqlScriptParser(PlainSqlScriptParser.INSTANCE);
    }

    public AbstractDialect(Driver driver) {
        this();
    }

    @Override
    public String getDatabaseId() {
        final Name nameAnno = (Name) Reflects.getAnnotation(this.getClass(), Name.class);
        String name;
        if (nameAnno != null) {
            name = nameAnno.value();
            if (Strings.isBlank(name)) {
                throw new IllegalStateException("@Name is empty in class" + this.getClass());
            }
        } else {
            final String simpleClassName = this.getClass().getSimpleName().toLowerCase();
            name = simpleClassName.replaceAll("dialect", "");
        }
        return name;
    }

    public final Properties getDefaultProperties() {
        return this.properties;
    }

    @Override
    public String toString() {
        return getClass().getName();
    }


    public LimitHandler getLimitHandler() {
        return getRealDialect().limitHandler;
    }

    protected void setLimitHandler(LimitHandler limitHandler) {
        limitHandler.setDialect(this);
        getRealDialect().limitHandler = limitHandler;
    }

    protected void setDelegate(@Nullable T delegate) {
        this.delegate = delegate;
    }

    protected void setUrlParser(@NonNull UrlParser urlParser) {
        Preconditions.checkNotNull(urlParser);
        if (urlParser instanceof CommonUrlParser) {
            ((CommonUrlParser) urlParser).setDialect(this);
        }
        getRealDialect().urlParser = urlParser;
    }

    protected void setLikeEscaper(@NonNull LikeEscaper likeEscaper) {
        likeEscaper = likeEscaper == null ? BackslashStyleEscaper.INSTANCE : likeEscaper;
        getRealDialect().likeEscaper = likeEscaper;
        this.likeEscaper = likeEscaper;
    }

    protected void setPlainSqlScriptParser(PlainSqlScriptParser parser){
        getRealDialect().plainSqlScriptParser = parser;
    }


    @Override
    public boolean isSupportsLimit() {
        return this.delegate == null ? false : this.delegate.isSupportsLimit();
    }

    @Override
    public boolean isSupportsLimitOffset() {
        return this.delegate == null ? isSupportsLimit() : this.delegate.isSupportsLimitOffset();
    }

    @Override
    public boolean isSupportsVariableLimit() {
        return this.delegate == null ? isSupportsLimit() : this.delegate.isSupportsVariableLimit();
    }

    @Override
    public boolean isSupportsVariableLimitInSubquery() {
        return this.delegate == null ? isSupportsVariableLimit() : this.delegate.isSupportsVariableLimitInSubquery();
    }

    @Override
    public void setUseLimitInVariableMode(boolean variableMode) {
        AbstractDialect d = getRealDialect();
        if (d.isSupportsVariableLimit()) {
            d.isUseLimitInVariableMode = variableMode;
        } else {
            d.isUseLimitInVariableMode = false;
        }
    }

    @Override
    public boolean isUseLimitInVariableMode() {
        return isUseLimitInVariableMode(false);
    }

    @Override
    public boolean isUseLimitInVariableMode(boolean isSubquery) {
        AbstractDialect d = getRealDialect();
        if (d.isUseLimitInVariableMode == null) {
            return isSubquery? d.isSupportsVariableLimitInSubquery() : d.isSupportsVariableLimit();
        } else {
            return d.isUseLimitInVariableMode;
        }
    }

    @Override
    public boolean isBindLimitParametersInReverseOrder() {
        return this.delegate == null ? false : this.delegate.isBindLimitParametersInReverseOrder();
    }

    @Override
    public boolean isBindLimitParametersFirst() {
        return this.delegate == null ? false : this.delegate.isBindLimitParametersFirst();
    }

    @Override
    public boolean isUseMaxForLimit() {
        return this.delegate == null ? false : this.delegate.isUseMaxForLimit();
    }

    @Override
    public boolean isForceLimitUsage() {
        return this.delegate == null ? false : this.delegate.isForceLimitUsage();
    }

    protected AbstractDialect getRealDialect() {
        if (delegate == null) {
            return this;
        } else {
            return delegate;
        }
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int position)
            throws SQLException {
        throw new UnsupportedOperationException(getClass().getName() + " does not support resultsets via stored procedures");
    }

    @Override
    public String getLimitSql(String sql, RowSelection selection) {
        return getLimitHandler().processSql(sql, selection);
    }

    @Override
    public String getLimitSql(String query, boolean isSubQuery, RowSelection rowSelection) {
        return this.getLimitSql(query, isSubQuery,true, rowSelection);
    }

    @Override
    public String getLimitSql(String query, boolean isSubQuery, boolean useLimitVariable, RowSelection rowSelection) {
        return getLimitHandler().processSql(query, isSubQuery, useLimitVariable, rowSelection);
    }

    @Override
    public void setMaxRows(RowSelection selection, PreparedStatement statement) throws SQLException {
        getLimitHandler().setMaxRows(selection, statement);
    }

    @Override
    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
        return getLimitHandler().bindLimitParametersAtEndOfQuery(selection, statement, index);
    }

    @Override
    public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
        return getLimitHandler().bindLimitParametersAtStartOfQuery(selection, statement, index);
    }

    public final List rebuildParameters(RowSelection selection, List queryParams){
        return rebuildParameters(false, selection, queryParams);
    }

    @Override
    public List rebuildParameters(boolean isSubquery, RowSelection selection, List queryParams) {
        return rebuildParameters(isSubquery, true, selection, queryParams);
    }

    @Override
    public List rebuildParameters(boolean isSubquery, boolean useLimitVaribale, RowSelection selection, List queryParams) {
        if(!useLimitVaribale || !isSupportsLimitOffset() || !isUseLimitInVariableMode(isSubquery)){
            return queryParams;
        }
        List result = Lists.newArrayList();
        int parameterIndex = 0;

        parameterIndex += this.rebuildLimitParametersAtStartOfQuery(selection, result, parameterIndex);
        if(!Objs.isEmpty(queryParams)){
            result.addAll(queryParams);
            parameterIndex+=queryParams.size();
        }
        parameterIndex += this.rebuildLimitParametersAtEndOfQuery(selection, result, parameterIndex);
        return result;
    }

    protected int rebuildLimitParametersAtEndOfQuery(RowSelection selection, List queryParams, int index)  {
        return getLimitHandler().rebuildLimitParametersAtEndOfQuery(selection, queryParams, index);
    }

    protected int rebuildLimitParametersAtStartOfQuery(RowSelection selection, List queryParams, int index) {
        return getLimitHandler().rebuildLimitParametersAtStartOfQuery(selection, queryParams, index);
    }

    public UrlParser getUrlParser() {
        return getRealDialect().urlParser;
    }

    protected TableGenerator createTableGenerator(DatabaseDescription databaseDescription) {
        return new CommonTableGenerator(databaseDescription, this);
    }

    public final String generateTableDDL(@NonNull DatabaseDescription database, String catalog, String schema, @NonNull String tableName) throws SQLException {
        Preconditions.checkNotNull(database);
        Preconditions.checkNotNull(tableName);
        Table table = new DatabaseLoader().loadTable(database, catalog, schema, tableName);
        if (table != null) {
            return createTableGenerator(database).generate(table);
        }
        throw new TableNonExistsException(StringTemplates.formatWithPlaceholder("Table {} is not exists", SQLs.getTableFQN(database, catalog, schema, tableName)));
    }

    private static String IDENTIFIER_BEFORE_QUOTES="\"'`[";
    private static String IDENTIFIER_AFTER_QUOTES="\"'`]";
    @Override
    public String getQuotedIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        if (delegate == null) {
            identifier = identifier.trim();
            identifier = Strings.stripStart(identifier, IDENTIFIER_BEFORE_QUOTES);
            identifier = Strings.stripEnd(identifier, IDENTIFIER_AFTER_QUOTES);
            return getBeforeQuote() + identifier + getAfterQuote();
        }
        return delegate.getQuotedIdentifier(identifier);
    }

    @Override
    public char getBeforeQuote() {
        return delegate == null ? '"' : delegate.getBeforeQuote();
    }

    @Override
    public char getAfterQuote() {
        return delegate == null ? '"' : delegate.getAfterQuote();
    }

    @Override
    public boolean isSupportsDistinct() {
        return delegate == null ? true : delegate.isSupportsDistinct();
    }

    @Override
    public boolean isSupportsBatchUpdates() {
        // default is true
        return delegate == null || delegate.isSupportsBatchUpdates();
    }

    @Override
    public boolean isSupportsBatchSql() {
        // default is true
        return delegate == null || delegate.isSupportsBatchSql();
    }

    @Override
    public List<Character> getLikeKeyChars() {
        return getRealDialect().likeEscaper.getLikeKeyChars();
    }

    @Override
    public String escape(String string) {
        return getRealDialect().likeEscaper.escape(string);
    }

    @Override
    public String appendmentAfterLikeClause() {
        return getRealDialect().likeEscaper.appendmentAfterLikeClause();
    }

    @Override
    public PlainSqlScriptParser getPlainSqlScriptParser() {
        return getRealDialect().plainSqlScriptParser;
    }
}
