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

    public static void main(final String[] args) {
        new MapMultiThreadedPerformanceTest().executeWithOutput();
    }

    @Override
    public void init(final ProgressionConfigurator config) {
        config.setConcurrencyLevel(32)
                .setBaseIterations(1_000)
                .setMaxStandardDeviation(25)
                .setTimeoutSeconds(100);
    }

    @Override
    public void addParameters(
            final ParametersContainer<Map<Integer, String>> parameters) {
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
    public void executeTests(
            final ParametrizedExecutor<Map<Integer, String>> executor) {

        executor.executeTest("CONCURRENT RANDOM READ",
                new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {

            @Override
            public void setUp(final Map<Integer, String> map) {
                fillUpMap(map, MAX_CAPACITY);
            }

            @Override
            protected Random createLocalObject() {
                return new Random(System.currentTimeMillis());
            }

            @Override
            public Object call(final Random rnd, final Map<Integer, String> map) {
                assertNotNull(map.get(rnd.nextInt(MAX_CAPACITY)));
                return map;
            }
        });

        executor.executeTest("CONCURRENT RANDOM WRITE",
                new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {

            @Override
            protected Random createLocalObject() {
                return new Random(System.currentTimeMillis());
            }

            @Override
            public Object call(final Random rnd, final Map<Integer, String> map) {
                map.put(rnd.nextInt(MAX_CAPACITY), "xyz");
                return map;
            }
        });
    }

    @Override
    public void addAssertions(final SuiteExecutionAssertion assertion) {
        assertion.forExecution("CONCURRENT RANDOM READ")
            .withPercentageTolerance(7)
            .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");

        assertion.forExecution("CONCURRENT RANDOM WRITE")
            .withPercentageTolerance(7)
            .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");
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
