package com.fillumina.performance;

import java.util.Map;

/**
 *
 * @author fra
 */
public class MapPerformanceApp {

    private static final int LOOPS = 1_000_000;
    private static final int MAX_CAPACITY = 128;

    public static void main(final String[] args) {
        // TODO use serie here
        //SingleThreadedMapPerformanceTest.execute(LOOPS * 10, MAX_CAPACITY);
        MultiThreadedMapPerformanceApp.test(LOOPS, MAX_CAPACITY);
    }


    public static void fillUpMap(final Map<Integer, String> map,
            final int maxCapacity) {
        map.clear();
        for (int i=0; i<maxCapacity; i++) {
            map.put(i, "xyz");
        }
    }

}
