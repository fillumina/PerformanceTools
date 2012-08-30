package com.fillumina.performance.producer.suite;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fra
 */
class CountingMap {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> map = new HashMap<>();

    public int size() {
        return map.size();
    }

    public void increment(final String key) {
        Integer counter = map.get(key);
        if (counter == null) {
            counter = 0;
        }
        map.put(key, counter + 1);
    }

    public int getCounterFor(final String key) {
        return map.get(key);
    }
}
