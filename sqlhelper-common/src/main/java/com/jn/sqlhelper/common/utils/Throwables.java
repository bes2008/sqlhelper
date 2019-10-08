package com.jn.sqlhelper.common.utils;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Throwables {
    private static final Logger logger = LoggerFactory.getLogger(Throwables.class);

    public static <I, O> O ignoreThrowable(@Nullable Logger logger,
                                           @Nullable O valueIfError,
                                           @NonNull ThrowableFunction<I, O> func,
                                           @Nullable I input) {
        try {
            return func.apply(input);
        } catch (Throwable ex) {
            logger = logger == null ? Throwables.logger : logger;
            logger.error(ex.getMessage(), ex);
            return valueIfError;
        }
    }

    public static <I1, I2, O> O ignoreThrowable(@Nullable Logger logger,
                                                @Nullable O valueIfError,
                                                @NonNull ThrowableFunction2<I1, I2, O> func,
                                                @Nullable I1 input1,
                                                @Nullable I2 input2) {
        try {
            return func.apply(input1, input2);
        } catch (Throwable ex) {
            logger = logger == null ? Throwables.logger : logger;
            logger.error(ex.getMessage(), ex);
            return valueIfError;
        }
    }

    public static <I, O> O ignoreExceptions(@Nullable Logger logger,
                                            @Nullable O valueIfError,
                                            @NonNull List<Class<Throwable>> throwables,
                                            @NonNull ThrowableFunction<I, O> func,
                                            @Nullable I input) {
        try {
            return func.apply(input);
        } catch (Throwable ex) {
            final Class exClass = ex.getClass();
            if (Collects.noneMatch(throwables, new Predicate<Class<Throwable>>() {
                @Override
                public boolean test(Class<Throwable> exceptionClass) {
                    return exceptionClass.isAssignableFrom(exClass);
                }
            })) {
                logger = logger == null ? Throwables.logger : logger;
                logger.error(ex.getMessage(), ex);
            }
            return valueIfError;
        }
    }

    public static <I1, I2, O> O ignoreExceptions(@Nullable Logger logger,
                                                 @Nullable O valueIfError,
                                                 @NonNull List<Class<Throwable>> throwables,
                                                 @NonNull ThrowableFunction2<I1, I2, O> func,
                                                 @Nullable I1 input1,
                                                 @Nullable I2 input2) {
        try {
            return func.apply(input1, input2);
        } catch (Throwable ex) {
            final Class exClass = ex.getClass();
            if (Collects.noneMatch(throwables, new Predicate<Class<Throwable>>() {
                @Override
                public boolean test(Class<Throwable> exceptionClass) {
                    return exceptionClass.isAssignableFrom(exClass);
                }
            })) {
                logger = logger == null ? Throwables.logger : logger;
                logger.error(ex.getMessage(), ex);
            }
            return valueIfError;
        }
    }
}
