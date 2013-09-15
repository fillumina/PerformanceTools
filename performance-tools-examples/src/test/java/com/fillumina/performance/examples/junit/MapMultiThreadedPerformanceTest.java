package com.fillumina.performance.examples.junit;

import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.producer.suite.ThreadLocalParametrizedRunnable;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.util.junit.JUnitSuitePerformanceTemplate;
import com.fillumina.performance.template.ProgressionConfigurator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MapMultiThreadedPerformanceTest
        extends JUnitSuitePerformanceTemplate<Map<Integer, String>> {
    private static final int MAX_CAPACITY = 128;
    private int maxCapacity;

    public static void main(final String[] args) {
        new MapMultiThreadedPerformanceTest().testWithOutput();
    }

    @Override
    public void init(ProgressionConfigurator builder) {
        this.maxCapacity = MAX_CAPACITY;
        builder.setConcurrencyLevel(32)
                .setBaseIterations(1_000)
                .setMaxStandardDeviation(25)
                .setTimeoutSeconds(100);
    }

    @Override
    public void addObjects(ParametrizedPerformanceSuite<Map<Integer, String>> suite) {
        suite.addParameter("SynchronizedLinkedHashMap",
                Collections.synchronizedMap(
                    new LinkedHashMap<Integer, String>(maxCapacity)));

        suite.addParameter("ConcurrentHashMap",
                new ConcurrentHashMap<Integer, String>(maxCapacity));

        suite.addParameter("SynchronizedHashMap",
                Collections.synchronizedMap(
                    new HashMap<Integer, String>(maxCapacity)));
    }

    @Override
    public void executeTests(
            final ParametrizedPerformanceSuite<Map<Integer, String>> suite) {

        suite.executeTest("CONCURRENT RANDOM READ",
                new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {

            @Override
            public void setUp(final Map<Integer, String> map) {
                fillUpMap(map, maxCapacity);
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

        suite.executeTest("CONCURRENT RANDOM WRITE",
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

    @Override
    public void addAssertions(AssertPerformanceForExecutionSuite apSuite) {
        apSuite.forExecution("CONCURRENT RANDOM READ")
            .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");

        apSuite.forExecution("CONCURRENT RANDOM WRITE")
            .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");
    }

    private static void fillUpMap(final Map<Integer, String> map,
            final int maxCapacity) {
        map.clear();
        for (int i=0; i<maxCapacity; i++) {
            map.put(i, "xyz");
        }
    }
}
