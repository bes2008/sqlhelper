package com.jn.sqlhelper.common.utils;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Supplier;

import java.io.Serializable;
import java.util.*;

public class LinkedCaseInsensitiveMap<V> implements Map<String, V>, Serializable, Cloneable {

    private final LinkedHashMap<String, V> targetMap;

    private final Locale locale;


    /**
     * Create a new LinkedCaseInsensitiveMap that stores case-insensitive keys
     * according to the default Locale (by default in lower case).
     *
     * @see #convertKey(String)
     */
    public LinkedCaseInsensitiveMap() {
        this((Locale) null);
    }

    /**
     * Create a new LinkedCaseInsensitiveMap that stores case-insensitive keys
     * according to the given Locale (by default in lower case).
     *
     * @param locale the Locale to use for case-insensitive key conversion
     * @see #convertKey(String)
     */
    public LinkedCaseInsensitiveMap(@Nullable Locale locale) {
        this(16, locale);
    }

    /**
     * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap}
     * with the given initial capacity and stores case-insensitive keys
     * according to the default Locale (by default in lower case).
     *
     * @param initialCapacity the initial capacity
     * @see #convertKey(String)
     */
    public LinkedCaseInsensitiveMap(int initialCapacity) {
        this(initialCapacity, null);
    }

    /**
     * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap}
     * with the given initial capacity and stores case-insensitive keys
     * according to the given Locale (by default in lower case).
     *
     * @param initialCapacity the initial capacity
     * @param locale          the Locale to use for case-insensitive key conversion
     * @see #convertKey(String)
     */
    public LinkedCaseInsensitiveMap(int initialCapacity, @Nullable Locale locale) {
        this.targetMap = new LinkedHashMap<String, V>(initialCapacity);
        this.locale = (locale != null ? locale : Locale.getDefault());
    }

    /**
     * Copy constructor.
     */
    @SuppressWarnings("unchecked")
    private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
        this.targetMap = (LinkedHashMap<String, V>) other.targetMap.clone();
        this.locale = other.locale;
    }


    // Implementation of java.util.Map

    @Override
    public int size() {
        return this.targetMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.targetMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String && this.targetMap.containsKey(convertKey((String) key)));
    }

    @Override
    public boolean containsValue(Object value) {
        return this.targetMap.containsValue(value);
    }

    @Override
    @Nullable
    public V get(Object key) {
        if (key instanceof String) {
            String caseInsensitiveKey = convertKey((String) key);
            if (caseInsensitiveKey != null) {
                return this.targetMap.get(caseInsensitiveKey);
            }
        }
        return null;
    }

    @Nullable
    public V getOrDefault(Object key, V defaultValue) {
        V v = get(key);
        if (v == null) {
            return defaultValue;
        }
        return v;
    }

    @Override
    @Nullable
    public V put(String key, @Nullable V value) {
        String internalKey = convertKey((String) key);
        return this.targetMap.put(internalKey, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }
        Collects.forEach(map, new Consumer2<String, V>() {
            @Override
            public void accept(String key, V value) {
                put(key, value);
            }
        });
    }

    @Override
    @Nullable
    public V remove(Object key) {
        if (key instanceof String) {
            String caseInsensitiveKey = convertKey((String) key);
            if (caseInsensitiveKey != null) {
                return this.targetMap.remove(caseInsensitiveKey);
            }
        }
        return null;
    }

    @Override
    public void clear() {
        this.targetMap.clear();
    }

    @Nullable
    public V putIfAbsent(String key, @Nullable V value) {
        String internalKey = convertKey(key);
        V oldValue = targetMap.get(internalKey);
        if (oldValue == null) {
            targetMap.put(internalKey, value);
        }
        return oldValue;
    }

    @Nullable
    public V supplyIfAbsent(String key, @NonNull Supplier<? super String, ? extends V> supplier) {
        String internalKey = convertKey(key);
        V oldValue = targetMap.get(internalKey);
        if (oldValue == null) {
            targetMap.put(internalKey, supplier.get(key));
        }
        return oldValue;
    }


    @Override
    public Set<String> keySet() {
        return this.targetMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.targetMap.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return this.targetMap.entrySet();
    }

    @Override
    public LinkedCaseInsensitiveMap<V> clone() {
        return new LinkedCaseInsensitiveMap<V>(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this.targetMap.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.targetMap.hashCode();
    }

    @Override
    public String toString() {
        return this.targetMap.toString();
    }


    // Specific to LinkedCaseInsensitiveMap

    /**
     * Return the locale used by this {@code LinkedCaseInsensitiveMap}.
     * Used for case-insensitive key conversion.
     *
     * @see #LinkedCaseInsensitiveMap(Locale)
     * @see #convertKey(String)
     * @since 4.3.10
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Convert the given key to a case-insensitive key.
     * <p>The default implementation converts the key
     * to lower-case according to this Map's Locale.
     *
     * @param key the user-specified key
     * @return the key to use for storing
     * @see String#toLowerCase(Locale)
     */
    protected String convertKey(String key) {
        return key.toLowerCase(getLocale());
    }

    /**
     * Determine whether this map should remove the given eldest entry.
     *
     * @param eldest the candidate entry
     * @return {@code true} for removing it, {@code false} for keeping it
     */
    protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
        return false;
    }

}