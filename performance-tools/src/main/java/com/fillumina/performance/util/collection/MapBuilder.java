package com.fillumina.performance.util.collection;

import com.fillumina.performance.util.CallBackBuilder;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MapBuilder<C, K, V> extends CallBackBuilder<C, Map<K, V>> {

    private final Map<K, V> map;
    private Class<K> keyClass;
    private Class<V> valueClass;

    public MapBuilder(Map<K, V> map) {
        super();
        this.map = map;
    }

    public MapBuilder(Map<K, V> map, C caller) {
        super(caller);
        this.map = map;
    }

    public MapBuilder(Map<K, V> map, Setter<C, Map<K, V>> setter) {
        super(setter);
        this.map = map;
    }

    public MapBuilder<C, K, V> keyClass(final Class<K> value) {
        this.keyClass = value;
        return this;
    }

    public MapBuilder<C, K, V> valueClass(final Class<V> value) {
        this.valueClass = value;
        return this;
    }

    public MapBuilder<C, K, V> put(K key, V value) {
        check(key, value);
        map.put(key, value);
        return this;
    }

    private void check(K key, V value) {
        if (keyClass != null && !keyClass.isAssignableFrom(key.getClass())) {
            throw new ClassCastException(requiredMsg("key", keyClass,
                    key.getClass()));
        }
        if (valueClass != null && value != null &&
                !valueClass.isAssignableFrom(value.getClass())) {
            throw new ClassCastException(requiredMsg("value", valueClass,
                    value.getClass()));
        }
    }

    private String requiredMsg(String type,
            Class<?> required,
            Class<?> provided) {
        return "trying to insert a not compatible " + type +
                ", required: " + required.getCanonicalName() +
                ", provided: " + provided.getCanonicalName();
    }

    @Override
    public Map<K, V> build() {
        return map;
    }

}
