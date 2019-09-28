package com.jn.sqlhelper.common.utils;

import com.jn.langx.util.function.Function2;

public abstract class ThrowableFunction2<I1, I2, O> implements Function2<I1, I2, O> {
    @Override
    public O apply(I1 i1, I2 i2) {
        try {
            return doFun2(i1, i2);
        } catch (Throwable ex) {
            throw com.jn.langx.util.Throwables.wrapAsRuntimeException(ex);
        }
    }

    public abstract O doFun2(I1 i1, I2 i2) throws Throwable;
}
