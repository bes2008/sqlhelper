package com.jn.sqlhelper.dialect.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class NonDistinctTreeSet<E> extends TreeSet<E> {
    public NonDistinctTreeSet() {
        super();
    }

    public NonDistinctTreeSet(Comparator<? super E> comparator) {
        super(new NonZeroComparator(comparator));
    }

    public NonDistinctTreeSet(Collection<? extends E> c) {
        super(c);
    }

    public NonDistinctTreeSet(SortedSet<E> s) {
        this(new NonZeroComparator<E>(s.comparator()));
        addAll(s);
    }
}
