package com.jn.sqlhelper.common.statement;

import com.jn.langx.annotation.Nullable;

public class CallableOutParameter {

    private final int sqlType;

    /**
     * the parameter name
     */
    @Nullable
    private String name;
    private Integer scale;

    /**
     * 该参数是否为 INOUT的，也即是否支持 in
     */
    private boolean inout = false;
    private Object inValue;

    public CallableOutParameter(int sqlType) {
        this(sqlType, null);
    }

    public CallableOutParameter(int sqlType, String name) {
        this(sqlType, name, null);
    }

    public CallableOutParameter(int sqlType, String name, Integer scale) {
        this(sqlType, name, scale, null);
    }

    public CallableOutParameter(int sqlType, String name, Integer scale, Object inValue) {
        this.sqlType = sqlType;
        this.name = name;
        this.scale = scale;
        if (inValue != null) {
            this.setInValue(inValue);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSqlType() {
        return sqlType;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public boolean isInout() {
        return inout;
    }

    public void setInValue(Object inValue) {
        this.inout = true;
        this.inValue = inValue;
    }

    public Object getInValue() {
        return inValue;
    }
}
