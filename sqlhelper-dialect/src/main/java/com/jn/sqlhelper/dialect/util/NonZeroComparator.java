package com.jn.sqlhelper.dialect.util;

import com.jn.langx.util.Preconditions;

import java.util.Comparator;

public class NonZeroComparator<E> implements Comparator<E> {
    private Comparator<E> delegate;

    public NonZeroComparator(Comparator comparator) {
        Preconditions.checkNotNull(comparator);
        this.delegate = comparator;
    }

    @Override
    public int compare(E o1, E o2) {
        int delta = delegate.compare(o1, o2);
        return delta == 0 ? 1 : delta;
    }
}
