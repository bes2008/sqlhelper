package com.fjn.helper.sql.dialect;

public enum NullPrecedence
{
    NONE, 
    FIRST, 
    LAST;
    
    public static NullPrecedence parse(final String name) {
        if ("none".equalsIgnoreCase(name)) {
            return NullPrecedence.NONE;
        }
        if ("first".equalsIgnoreCase(name)) {
            return NullPrecedence.FIRST;
        }
        if ("last".equalsIgnoreCase(name)) {
            return NullPrecedence.LAST;
        }
        return null;
    }
    
    public static NullPrecedence parse(final String name, final NullPrecedence defaultValue) {
        final NullPrecedence value = parse(name);
        return (value != null) ? value : defaultValue;
    }
}
