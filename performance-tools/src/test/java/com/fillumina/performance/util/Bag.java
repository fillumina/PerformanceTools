package com.fillumina.performance.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple collection that counts the number of times an object appears in it.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Bag<T> {
    private static final long serialVersionUID = 1L;

    private Map<T, Long> map = new HashMap<>();

    public int size() {
        return map.size();
    }

    public void add(final T key) {
        Long counter = map.get(key);
        if (counter == null) {
            counter = 0L;
        }
        map.put(key, counter + 1);
    }

    public long getCount(final T key) {
        final Long value = map.get(key);
        return value == null ? 0 : value;
    }
}
