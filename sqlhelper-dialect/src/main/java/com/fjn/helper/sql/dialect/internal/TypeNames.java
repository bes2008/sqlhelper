package com.fjn.helper.sql.dialect.internal;

import com.fjn.helper.sql.dialect.SQLDialectException;
import com.fjn.helper.sql.dialect.StringHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class TypeNames {
    private Map<Integer, String> defaults = new HashMap();


    private Map<Integer, Map<Long, String>> weighted = new HashMap();


    public String get(int typeCode)
            throws SQLDialectException {
        String result = (String) this.defaults.get(Integer.valueOf(typeCode));
        if (result == null) {
            throw new SQLDialectException("No Dialect mapping for JDBC type: " + typeCode);
        }
        return result;
    }


    public String get(int typeCode, long size, int precision, int scale)
            throws SQLDialectException {
        Map<Long, String> map = (Map) this.weighted.get(Integer.valueOf(typeCode));
        if ((map != null) && (map.size() > 0)) {
            for (Entry<Long, String> entry : map.entrySet()) {
                if (size <= ((Long) entry.getKey()).longValue()) {
                    return replace((String) entry.getValue(), size, precision, scale);
                }
            }
        }


        return replace(get(typeCode), size, precision, scale);
    }

    private static String replace(String type, long size, int precision, int scale) {
        type = StringHelper.replaceOnce(type, "$s", Integer.toString(scale));
        type = StringHelper.replaceOnce(type, "$l", Long.toString(size));
        return StringHelper.replaceOnce(type, "$p", Integer.toString(precision));
    }


    public void put(int typeCode, long capacity, String value) {
        Map<Long, String> map = (Map) this.weighted.get(Integer.valueOf(typeCode));
        if (map == null) {
            map = new TreeMap();
            this.weighted.put(Integer.valueOf(typeCode), map);
        }
        map.put(Long.valueOf(capacity), value);
    }


    public void put(int typeCode, String value) {
        this.defaults.put(Integer.valueOf(typeCode), value);
    }


    public boolean containsTypeName(String typeName) {
        return this.defaults.containsValue(typeName);
    }
}