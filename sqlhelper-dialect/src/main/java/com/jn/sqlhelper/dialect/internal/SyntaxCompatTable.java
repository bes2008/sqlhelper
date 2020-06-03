package com.jn.sqlhelper.dialect.internal;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Supplier;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SyntaxCompatTable {
    private static final Map<String, Set<String>> mapping = Collects.emptyNonAbsentHashMap(new Supplier<String, Set<String>>() {
        @Override
        public Set<String> get(String s) {
            Set<String> set = new LinkedHashSet<String>();
            set.add(s);
            return set;
        }
    });

    /**
     * 很多国产数据库的语法其实就是直接拿 mysql, oracle, progresql的
     * @param databaseId
     * @param compatDatabaseId
     */
    public void register(String databaseId, String compatDatabaseId){
        mapping.get(databaseId).add(compatDatabaseId);
    }
}
