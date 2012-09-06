package com.fillumina.performance.examples;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import java.util.Map;

/**
 *
 * @author fra
 */
public class MapPerformanceApp {

    private static final int LOOPS = 1_000_000;
    private static final int MAX_CAPACITY = 128;

    public static void main(final String[] args) {
        final PerformanceConsumer presenter = StringTableViewer.CONSUMER;
        new MapSingleThreadedPerformanceTest().calculateSuite(LOOPS * 10, MAX_CAPACITY,
                presenter);
        new MapMultiThreadedPerformanceApp().execute(LOOPS, MAX_CAPACITY,
                presenter);
    }

    public static void fillUpMap(final Map<Integer, String> map,
            final int maxCapacity) {
        map.clear();
        for (int i=0; i<maxCapacity; i++) {
            map.put(i, "xyz");
        }
    }

}
