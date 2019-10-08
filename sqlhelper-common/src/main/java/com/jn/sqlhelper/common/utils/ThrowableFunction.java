package com.jn.sqlhelper.common.utils;


import com.jn.langx.util.function.Function;

public abstract class ThrowableFunction<I,O> implements Function<I,O> {
    @Override
    public O apply(I i) {
        try {
            return doFun(i);
        }catch (Throwable ex){
            throw com.jn.langx.util.Throwables.wrapAsRuntimeException(ex);
        }
    }

    public abstract O doFun(I i) throws Throwable;
}
