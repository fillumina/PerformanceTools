package com.fillumina.performance.util.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SymmetricMatrix<K,V>
        implements UnmodifiableSymmetricMatrix<K, V> {

    private final Map<K,Integer> indexes;
    private final V[] array;
    private Set<K> keys;
    private List<V> values;

    @SuppressWarnings("unchecked")
    public SymmetricMatrix(K... keys) {
        int size = keys.length;
        this.indexes = createMap(keys);
        this.array = (V[]) new Object[size * (size + 1) / 2];
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public Set<K> keySet() {
        if (keys == null) {
            keys = Collections.unmodifiableSet(indexes.keySet());
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        if (values == null) {
            values = Collections.unmodifiableList(Arrays.asList(array));
        }
        return values;
    }

    public void put(K k1, K k2, V value) {
        int x = getIndexOf(k1);
        int y = getIndexOf(k2);
        putByIndex(x, y, value);
    }

    public void putByIndex(int x, int y, V value) {
        array[index(x, y)] = value;
    }

    @Override
    public V get(K k1, K k2) {
        int x = getIndexOf(k1);
        int y = getIndexOf(k2);
        return array[index(x, y)];
    }

    static int index(int x, int y) {
        if (x < y) {
            return x + (y * (y + 1) / 2);
        } else {
            return y + (x * (x + 1) / 2);
        }

    }

    private int getIndexOf(K key) {
        try {
            return indexes.get(key);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("'" + key +
                    "' key doesn't exist, try with one of: " +
                    indexes.keySet());
        }
    }

    static <K> Map<K,Integer> createMap(K[] names) {
        int length = names.length;
        Map<K,Integer> map = new LinkedHashMap<>(length);

        for (int i=0; i<length; i++) {
            map.put(names[i], i);
        }

        return map;
    }
}
