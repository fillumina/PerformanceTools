package com.fillumina.performance.examples;

import java.util.Map;

/**
 *
 * @author fra
 */
public class MapFillerHelper {

    public static void fillUpMap(final Map<Integer, String> map,
            final int maxCapacity) {
        map.clear();
        for (int i=0; i<maxCapacity; i++) {
            map.put(i, "xyz");
        }
    }

}
