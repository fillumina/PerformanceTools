package com.fillumina.performance.examples.template;

import com.fillumina.performance.producer.suite.ThreadLocalParametrizedRunnable;
import com.fillumina.performance.consumer.assertion.SuiteExecutionAssertion;
import com.fillumina.performance.producer.suite.ParametersContainer;
import com.fillumina.performance.producer.suite.ParametrizedExecutor;
import com.fillumina.performance.util.junit.JUnitParametrizedPerformanceTemplate;
import com.fillumina.performance.template.ProgressionConfigurator;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MapMultiThreadedPerformanceTest
        extends JUnitParametrizedPerformanceTemplate<Map<Integer, String>> {
    private static final int MAX_CAPACITY = 128;
    private int maxCapacity;

    public static void main(final String[] args) {
        new MapMultiThreadedPerformanceTest().testWithOutput();
    }

    @Override
    public void init(final ProgressionConfigurator builder) {
        this.maxCapacity = MAX_CAPACITY;
        builder.setConcurrencyLevel(32)
                .setBaseIterations(1_000)
                .setMaxStandardDeviation(25)
                .setTimeoutSeconds(100);
    }

    @Override
    public void addParameters(
            final ParametersContainer<Map<Integer, String>> parameters) {
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
    public void executeTests(
            final ParametrizedExecutor<Map<Integer, String>> executor) {

        executor.executeTest("CONCURRENT RANDOM READ",
                new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {

            @Override
            public void setUp(final Map<Integer, String> map) {
                fillUpMap(map, maxCapacity);
            }

            @Override
            protected Random createLocalObject() {
                return new Random(System.currentTimeMillis());
            }

            @Override
            public Object call(final Random rnd, final Map<Integer, String> map) {
                assertNotNull(map.get(rnd.nextInt(maxCapacity)));
                return map;
            }
        });

        executor.executeTest("CONCURRENT RANDOM WRITE",
                new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {
            final Random rnd = new Random(System.currentTimeMillis());

            @Override
            protected Random createLocalObject() {
                return new Random(System.currentTimeMillis());
            }

            @Override
            public Object call(final Random rnd, final Map<Integer, String> map) {
                map.put(rnd.nextInt(maxCapacity), "xyz");
                return map;
            }
        });
    }

    @Override
    public void addAssertions(final SuiteExecutionAssertion assertion) {
        assertion.forExecution("CONCURRENT RANDOM READ")
            .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");

        assertion.forExecution("CONCURRENT RANDOM WRITE")
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
