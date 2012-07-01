package com.fillumina.performance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class MultiThreadedMapPerformanceApp {
    private static final int LOOPS = 1_000_000;
    private static final int MAX_CAPACITY = 128;

    public static void main(final String[] args) {
        test(LOOPS, MAX_CAPACITY);
    }

    public static void test(final int loops, final int maxCapacity) {
        final PerformanceSuite<Map<Integer,String>> suite =
                createMultiThreadPerformanceSuite(maxCapacity);

        suite.execute("CONCURRENT RANDOM READ", loops,
                new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {

            @Override
            public void setUp(final Map<Integer, String> map) {
                MapPerformanceApp.fillUpMap(map, maxCapacity);
            }

            @Override
            protected Random createLocalObject() {
                return new Random();
            }

            @Override
            public void call(final Random rnd, final Map<Integer, String> map) {
                assertNotNull(map.get(rnd.nextInt(maxCapacity)));
            }
        });

        suite.execute("CONCURRENT RANDOM WRITE", loops,
                new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {
            final Random rnd = new Random();

            @Override
            protected Random createLocalObject() {
                return new Random();
            }

            @Override
            public void call(final Random rnd, final Map<Integer, String> map) {
                map.put(rnd.nextInt(maxCapacity), "xyz");
            }
        });

    }

    @SuppressWarnings("unchecked")
    private static PerformanceSuite<Map<Integer,String>>
            createMultiThreadPerformanceSuite(final int maxCapacity) {

        final PerformanceSuite<Map<Integer,String>> suite =
                new PerformanceSuite<>(PerformanceTimerBuilder.createMultiThread()
                    .setConcurrencyLevel(8)
                    .setTaskNumber(8)
                    .setTimeout(25, TimeUnit.SECONDS)
                    .build());

        suite.addObjectToTest("SynchronizedLinkedHashMap",
                Collections.synchronizedMap(
                    new LinkedHashMap<Integer, String>(maxCapacity)));

        suite.addObjectToTest("ConcurrentHashMap",
                new ConcurrentHashMap<Integer, String>(maxCapacity));

        suite.addObjectToTest("SynchronizedHashMap",
                Collections.synchronizedMap(new HashMap<Integer, String>(maxCapacity)));

        return suite;
    }
}
