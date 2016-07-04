package com.fillumina.performance.examples.template;

import com.fillumina.performance.assertion.AssertParametrizedPerformance;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.template.TestConfigurator;
import com.fillumina.performance.util.junit.JUnitParametrizedPerformanceTemplate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A really naive test of several maps.
 *
 * @author Francesco Illuminati
 */
public class MapSingleThreadedPerformanceTest
        extends JUnitParametrizedPerformanceTemplate<Map<Integer, String>> {

    private static final int MAX_CAPACITY = 128;

    private int maxCapacity;

    public static void main(final String[] args) {
        new MapSingleThreadedPerformanceTest().executeWithFullOutput();
    }

    @Override
    public void config(TestConfigurator configuration) {
        maxCapacity = MAX_CAPACITY;
        configuration
                .setMinConfidence(0.4)
                .setMaxPercentageMargin(5)
                .setName("map single threaded")
                .setTimeoutSeconds(300);
    }

    @Override
    public void addParameters(
            final ParameterContainer<Map<Integer, String>> parameters) {

        parameters.addParameter("HashMap",
                new HashMap<Integer, String>(maxCapacity));

        parameters.addParameter("TreeMap",
                new TreeMap<Integer, String>());

        parameters.addParameter("LinkedHashMap",
                new LinkedHashMap<Integer, String>(maxCapacity));

        parameters.addParameter("WeakHashMap",
                new WeakHashMap<Integer, String>(maxCapacity));

        parameters.addParameter("SynchronizedLinkedHashMap",
                Collections.synchronizedMap(
                    new LinkedHashMap<Integer, String>(maxCapacity)));

        parameters.addParameter("ConcurrentHashMap",
                new ConcurrentHashMap<Integer, String>(maxCapacity));

        parameters.addParameter("SynchronizedHashMap",
                Collections.synchronizedMap(
                    new HashMap<Integer, String>(maxCapacity)));
    }

    @Override
    public void addTests(
            TestContainer<ParametrizedTestable<Map<Integer, String>>> tests) {
        // adds a probability to read an element which is not there
        final int maxCapacityPlusOne = maxCapacity + 1;

        tests.addTest("SEQUENTIAL READ", new FilledMapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % maxCapacityPlusOne, "xyz");
            }
        });

        tests.addTest("SEQUENTIAL WRITE", new MapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % maxCapacityPlusOne, "xyz");
            }
        });

        tests.addTest("RANDOM READ", new FilledMapTest(maxCapacity) {
            final Random rnd = new Random(System.currentTimeMillis());

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.get(rnd.nextInt(maxCapacityPlusOne));
            }
        });

        tests.addTest("RANDOM WRITE",
                new ParametrizedTestable<Map<Integer, String>>() {
            final Random rnd = new Random(System.currentTimeMillis());

            @Override
            public Object test(Map<Integer, String> map) {
                return map.put(rnd.nextInt(maxCapacityPlusOne), "xyz");
            }
        });
    }

    @Override
    public void addAssertions(
            AssertParametrizedPerformance<Void, SpeedStats> assertion) {
        final int tolerance = 5;
        assertion
            .forTest("SEQUENTIAL READ",
                    AssertSpeed.withTolerancePercentage(tolerance)
                    .assertOrder("TreeMap").greaterThan("HashMap"))

            .forTest("SEQUENTIAL WRITE",
                    AssertSpeed.withTolerancePercentage(tolerance)
                    .assertOrder("TreeMap").greaterThan("HashMap"))

            .forTest("RANDOM READ",
                    AssertSpeed.withTolerancePercentage(tolerance)
                    .assertOrder("TreeMap").greaterThan("HashMap"))

            .forTest("RANDOM WRITE",
                    AssertSpeed.withTolerancePercentage(tolerance)
                    .assertOrder("TreeMap").greaterThan("HashMap"));
    }

    private static abstract class MapTest
            extends ParametrizedTestable<Map<Integer, String>> {
        final int maxCapacity;
        int i=0;

        public MapTest(final int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        @Override
        public Object test(final Map<Integer, String> param) {
            call(param, i++ % maxCapacity);
            return null;
        }

        public abstract void call(final Map<Integer, String> param, final int i);
    }

    private static abstract class FilledMapTest extends MapTest {

        public FilledMapTest(int maxCapacity) {
            super(maxCapacity);
        }

        @Override
        public void setUp(final Map<Integer, String> map) {
            map.clear();
            final List<Integer> list = new ArrayList<>(maxCapacity);
            for (int i=0; i<maxCapacity; i++) {
                list.add(i);
            }
            Collections.shuffle(list, new Random(System.currentTimeMillis()));
            for (int i=0; i<maxCapacity; i++) {
                map.put(list.get(i), "xyz");
            }
        }
    }
}
