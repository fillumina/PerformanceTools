package com.fillumina.performance.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Concise way to create maps.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Mapper {

    /**
     * Helper to easily create a {@link LinkedHashMap}.
     * <code><pre>
     * Map<String,Double> map = Mapper.create("a", 1.2, "b", 2.3, "c", 3.4);
     * </pre></code>
     * @param <T>     the type of the values in the map
     * @param objects the objects (must conform to types in pairs)
     * @return        an {@link HashMap} filled with the given couples.
     */
    @SuppressWarnings("unchecked")
    public static <T> LinkedHashMap<String, T> create(Object... objects) {
        final LinkedHashMap<String,T> map = new LinkedHashMap<>();
        for (int i=0; i<objects.length; i+=2) {
            map.put((String)objects[i], (T) objects[i+1]);
        }
        return map;
    }
}
