package com.fillumina.performance.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
public class CountingMap<T> {
    private static final long serialVersionUID = 1L;

    private Map<T, Long> map = new HashMap<>();

    public int size() {
        return map.size();
    }

    public void increment(final T key) {
        Long counter = map.get(key);
        if (counter == null) {
            counter = 0L;
        }
        map.put(key, counter + 1);
    }

    public long getCounterFor(final T key) {
        final Long value = map.get(key);
        return value == null ? 0 : value;
    }
}
