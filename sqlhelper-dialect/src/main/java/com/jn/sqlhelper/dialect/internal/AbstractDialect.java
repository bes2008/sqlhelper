package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.dialect.*;
import com.jn.sqlhelper.dialect.likeescaper.BackslashStyleEscaper;
import com.jn.sqlhelper.dialect.internal.limit.DefaultLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHandler;
import com.jn.sqlhelper.dialect.urlparser.CommonUrlParser;
import com.jn.sqlhelper.dialect.urlparser.NoopUrlParser;
import com.jn.sqlhelper.dialect.urlparser.UrlParser;
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


    private static String IDENTIFIER_BEFORE_QUOTES="\"'`[";
    private static String IDENTIFIER_AFTER_QUOTES="\"'`]";

    @Override
    public final String getUnquoteIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        identifier = identifier.trim();
        identifier = Strings.stripStart(identifier, IDENTIFIER_BEFORE_QUOTES);
        identifier = Strings.stripEnd(identifier, IDENTIFIER_AFTER_QUOTES);

        return identifier;
    }

    @Override
    public final String getQuotedIdentifier(String identifier) {
        return getQuotedIdentifier(identifier, null);
    }

    public final String getQuotedIdentifier(String identifier, IdentifierCase identifierCase) {
        if (identifier == null) {
            return null;
        }
        if (delegate == null) {
            identifier = getUnquoteIdentifier(identifier);

            if(identifierCase==null){
                identifierCase = unquotedIdentifierCase();
            }
            switch (identifierCase){
                case LOWER_CASE:
                    identifier = Strings.lowerCase(identifier);
                    break;
                case UPPER_CASE:
                    identifier = Strings.upperCase(identifier);
                    break;
                case NO_CASE:
                case IGNORE_CASE:
                default:
                    break;
            }

            if(Strings.contains(identifier, ".")){
                return identifier;
            }
            return getBeforeQuote() + identifier + getAfterQuote();
        }
        return delegate.getQuotedIdentifier(identifier, identifierCase);
    }

    @Override
    public IdentifierCase unquotedIdentifierCase() {
        return IdentifierCase.NO_CASE;
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
