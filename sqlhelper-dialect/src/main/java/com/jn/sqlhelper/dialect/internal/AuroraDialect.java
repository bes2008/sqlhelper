package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.util.ClassLoaders;
import com.jn.sqlhelper.dialect.likeescaper.LikeEscaper;
import com.jn.sqlhelper.dialect.internal.limit.LimitCommaLimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitHandler;
import com.jn.sqlhelper.dialect.internal.limit.LimitOffsetLimitHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Amazon Aurora
 */
public class AuroraDialect extends AbstractDialect {
    enum WorkingWith {
        MySQL,
        PostgreSQL
    }

    private WorkingWith workingWith;

    private static final Map<WorkingWith, LimitHandler> limitHandlerMap = new HashMap<WorkingWith, LimitHandler>();
    private static final Map<WorkingWith, LikeEscaper> likeEscaperMap = new HashMap<WorkingWith, LikeEscaper>();

    static {
        limitHandlerMap.put(WorkingWith.MySQL, new LimitCommaLimitHandler());
        limitHandlerMap.put(WorkingWith.PostgreSQL, new LimitOffsetLimitHandler());
    }

    public AuroraDialect() {
        super();
        if (findMySQLDriver()) {
            workingWith = WorkingWith.MySQL;
        } else if (findPostgreSQLDriver()) {
            workingWith = WorkingWith.PostgreSQL;
        }
        if(workingWith==null){
            workingWith=WorkingWith.MySQL;
        }
        setLimitHandler(limitHandlerMap.get(workingWith));
    }

    private static boolean findMySQLDriver() {
        return ClassLoaders.hasClass("com.mysql.jdbc.Driver", AuroraDialect.class.getClassLoader()) || ClassLoaders.hasClass("com.mysql.cj.jdbc.Driver", AuroraDialect.class.getClassLoader()) ;
    }

    private static boolean findPostgreSQLDriver() {
        return ClassLoaders.hasClass("org.postgresql.Driver", AuroraDialect.class.getClassLoader());
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
        boolean reverse = false;
        switch (workingWith) {
            case MySQL:
                reverse = false;
                break;
            case PostgreSQL:
                reverse = true;
                break;
            default:
                reverse = false;
                break;
        }
        return reverse;
    }
}
