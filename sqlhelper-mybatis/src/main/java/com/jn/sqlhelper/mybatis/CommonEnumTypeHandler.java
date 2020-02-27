package com.jn.sqlhelper.mybatis;

import com.jn.langx.Delegatable;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;
import com.jn.langx.util.reflect.Reflects;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Just for any enum what implements the {@link CommonEnum} interface
 * @param <E>
 */
public class CommonEnumTypeHandler<E extends Enum<E> & Delegatable<EnumDelegate>> extends BaseTypeHandler<E> {
    private Class<E> enumType;

    public CommonEnumTypeHandler() {
    }

    public void setEnumType(Class<E> enumType) {
        if (enumType != null) {
            if (Reflects.isSubClassOrEquals(Enum.class, enumType)) {
                this.enumType = enumType;
            }
        }
    }

    public CommonEnumTypeHandler(@Nullable Class<E> enumType) {
        setEnumType(enumType);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getDelegate().getCode());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return Enums.ofCode(enumType, code);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return Enums.ofCode(enumType, code);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return Enums.ofCode(enumType, code);
    }
}
