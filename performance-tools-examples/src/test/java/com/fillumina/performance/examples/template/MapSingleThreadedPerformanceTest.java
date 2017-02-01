package com.fillumina.performance.examples.template;

import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.template.AutoParameterizedPerformanceTemplate;
import com.fillumina.performance.template.ParameterizedAssertion;
import com.fillumina.performance.template.TestConfiguration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;

/**
 * A really naive test of several maps.
 *
 * @author Francesco Illuminati
 */
public class MapSingleThreadedPerformanceTest
        extends AutoParameterizedPerformanceTemplate<Map<Integer, String>> {

    private static final int MAX_CAPACITY = 128;

    private PrintOut printOut = new PrintOut();

    private int maxCapacity;

    public static void main(final String[] args) {
        final MapSingleThreadedPerformanceTest test =
                new MapSingleThreadedPerformanceTest();
        test.printOut = new PrintOut(true);
        test.executeWithFullOutput();
    }

    @Test
    public void executeTest() {
        if (printOut.isPrintOut()) {
            executeWithFullOutput();
        } else {
            executeWithoutOutput();
        }
    }

    @Override
    public void config(TestConfiguration configuration) {
        maxCapacity = MAX_CAPACITY;
        configuration
            .setName("map single threaded")
                .speedTestOnly()
                    .setMaxPercentageMargin(5)
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
            TestContainer<ParameterizedTestable<Map<Integer, String>>> tests) {
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
                new ParameterizedTestable<Map<Integer, String>>() {
            final Random rnd = new Random(System.currentTimeMillis());

            @Override
            public Object test(Map<Integer, String> map) {
                return map.put(rnd.nextInt(maxCapacityPlusOne), "xyz");
            }
        });
    }

    @Override
    public void addAssertions(ParameterizedAssertion assertion) {
        final int tolerance = 5;
        assertion.speed()
            .forTest("SEQUENTIAL READ",
                    AssertSpeed.withTolerance(tolerance)
                    .assertOrder("TreeMap").greaterThan("HashMap"))

            .forTest("SEQUENTIAL WRITE",
                    AssertSpeed.withTolerance(tolerance)
                    .assertOrder("TreeMap").greaterThan("HashMap"))

            .forTest("RANDOM READ",
                    AssertSpeed.withTolerance(tolerance)
                    .assertOrder("TreeMap").greaterThan("HashMap"))

            .forTest("RANDOM WRITE",
                    AssertSpeed.withTolerance(tolerance)
                    .assertOrder("TreeMap").greaterThan("HashMap"));
    }

    private static abstract class MapTest
            extends ParameterizedTestable<Map<Integer, String>> {
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
