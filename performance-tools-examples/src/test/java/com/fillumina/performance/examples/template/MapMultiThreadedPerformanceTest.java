package com.fillumina.performance.examples.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.suite.assertion.AssertParametrizedPerformance;
import com.fillumina.performance.template.TestConfigurator;
import com.fillumina.performance.util.junit.JUnitParametrizedPerformanceTemplate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati
 */
public class MapMultiThreadedPerformanceTest
        extends JUnitParametrizedPerformanceTemplate<Map<Integer, String>> {
    private static final int MAX_CAPACITY = 128;

    public static void main(final String[] args) {
        new MapMultiThreadedPerformanceTest().executeWithFullOutput();
    }

    @Override
    public void config(TestConfigurator configuration) {
        configuration
                .setMessage("Map Multi Threaded")
                .setConcurrencyLevel(32)
                .setBaseIterations(1_000)
                .setTimeoutSeconds(100);
    }

    @Override
    public void addParameters(
            final ParameterContainer<Map<Integer, String>> parameters) {
        parameters.addParameter("SynchronizedLinkedHashMap",
                Collections.synchronizedMap(
                    new LinkedHashMap<Integer, String>(MAX_CAPACITY)));

        parameters.addParameter("ConcurrentHashMap",
                new ConcurrentHashMap<Integer, String>(MAX_CAPACITY));

        parameters.addParameter("SynchronizedHashMap",
                Collections.synchronizedMap(
                    new HashMap<Integer, String>(MAX_CAPACITY)));
    }

    @Override
    public void addTests(
            TestContainer<ParametrizedTestable<Map<Integer, String>>> tests) {

        tests.addTest("CONCURRENT RANDOM READ",
                new ParametrizedTestable<Map<Integer, String>>() {

            @Override
            public void setUp(final Map<Integer, String> map) {
                fillUpMap(map, MAX_CAPACITY);
            }

            @Override
            public Object test(final Map<Integer, String> map) {
                assertNotNull(map.get(
                        ThreadLocalRandom.current().nextInt(MAX_CAPACITY)));
                return map;
            }
        });

        tests.addTest("CONCURRENT RANDOM WRITE",
                new ParametrizedTestable<Map<Integer, String>>() {

            @Override
            public Object test(final Map<Integer, String> map) {
                map.put(ThreadLocalRandom.current().nextInt(MAX_CAPACITY), "xyz");
                return map;
            }
        });
    }

    @Override
    public void addAssertions(
            AssertParametrizedPerformance<?> assertion) {
        assertion
            .forTest("CONCURRENT RANDOM READ",
                AssertPerformance
                        .withTolerance(7)
                        .assertSpeed("SynchronizedHashMap")
                        .slowerThan("ConcurrentHashMap"))

            .forTest("CONCURRENT RANDOM WRITE",
                AssertPerformance
                        .withTolerance(7)
                        .assertSpeed("SynchronizedHashMap")
                        .slowerThan("ConcurrentHashMap"));
    }

    private static void fillUpMap(final Map<Integer, String> map,
            final int maxCapacity) {
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
