package com.fjn.helper.sql.util;

public class Holder<V> {
    private V v;

    public Holder() {
    }

    public Holder(final V value) {
        this.set(value);
    }

    public void set(final V value) {
        this.v = value;
    }

    public V get() {
        return this.v;
    }

    public void setV(final V value) {
        this.v = value;
    }

    public V getV() {
        return this.v;
    }
}
