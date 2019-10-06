package com.jn.sqlhelper.common.utils;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.struct.Holder;

import java.util.Collection;

public class Utils {
    /**
     * Iterate every element
     */
    public static <E> void forEach(@Nullable Collection<E> collection, @NonNull final Consumer2<Integer, E> consumer) {
        Preconditions.checkNotNull(consumer);
        if (Emptys.isNotEmpty(collection)) {
            final Holder<Integer> indexHolder = new Holder<Integer>(-1);
            Collects.forEach(collection, new Consumer<E>() {
                @Override
                public void accept(E e) {
                    indexHolder.set(indexHolder.get() + 1);
                    consumer.accept(indexHolder.get(), e);
                }
            });
        }
    }
}
