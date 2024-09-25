package com.jn.sqlhelper.dialect;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

public enum SqlCompatibilityType implements CommonEnum {
    ORACLE(1,"oracle","oracle"),
    MYSQL(2,"mysql","mysql"),
    TERADATA(3,"teradata","teradata"),
    POSTGRESQL(4,"postgresql","postgresql"),
    SQLSERVER(5,"sqlserver","sqlserver"),
    SQL92(12,"sql92","sql92"),


    NON_COMPATIBILITY(100,"non_compatibility","non_compatibility");

    private EnumDelegate delegate;
    SqlCompatibilityType(int code, String name ,String displayText){
        this.delegate = new EnumDelegate(code, name, displayText);
    }

    @Override
    public int getCode() {
        return this.delegate.getCode();
    }

    @Override
    public String getDisplayText() {
        return this.delegate.getDisplayText();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }
}
