package com.jn.sqlhelper.common.utils;

public class HashCodeBuilder {
    private int hash = 0;

    public HashCodeBuilder with(Object object) {
        compute(object == null ? 0 : object.hashCode());
        return this;
    }

    private void compute(int hash) {
        this.hash = this.hash * 31 + hash;
    }

    public int build() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashCodeBuilder that = (HashCodeBuilder) o;

        return hash == that.hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
