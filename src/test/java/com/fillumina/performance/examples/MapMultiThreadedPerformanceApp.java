package com.fillumina.performance.examples;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.suite.ThreadLocalParametrizedRunnable;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.util.junit.JUnitPerformanceTestRunner;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author fra
 */
@RunWith(JUnitPerformanceTestRunner.class)
public class MapMultiThreadedPerformanceApp {
    private static final int LOOPS = 1_000_000;
    private static final int MAX_MAP_CAPACITY = 128;

    public static void main(final String[] args) {
        final MapMultiThreadedPerformanceApp test =
                new MapMultiThreadedPerformanceApp();
        test.execute(LOOPS, MAX_MAP_CAPACITY, StringTableViewer.CONSUMER);
    }

    public void test() {
        execute(LOOPS, MAX_MAP_CAPACITY, createAssertSuite());
    }

    //TODO loops!
    public void execute(final int loops, final int maxMapCapacity,
            final PerformanceConsumer performanceConsumer) {

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
                createMultiThreadPerformanceSuite(maxMapCapacity);

        suite.addPerformanceConsumer(performanceConsumer);

        suite.executeTest("CONCURRENT RANDOM READ",
                new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {

            @Override
            public void setUp(final Map<Integer, String> map) {
                MapFillerHelper.fillUpMap(map, maxMapCapacity);
            }

            @Override
            protected Random createLocalObject() {
                return new Random();
            }

            @Override
            public void call(final Random rnd, final Map<Integer, String> map) {
                assertNotNull(map.get(rnd.nextInt(maxMapCapacity)));
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
                map.put(rnd.nextInt(maxMapCapacity), "xyz");
            }
        });

    }

    @SuppressWarnings("unchecked")
    private static ParametrizedPerformanceSuite<Map<Integer,String>>
            createMultiThreadPerformanceSuite(final int maxMapCapacity) {

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
            PerformanceTimerBuilder.createMultiThread()
                .setConcurrencyLevel(8)
                .setWorkerNumber(8)
                .setTimeout(25, TimeUnit.SECONDS)
                .build()
                .instrumentedBy(new ParametrizedPerformanceSuite<Map<Integer,String>>());

        suite.addObjectToTest("SynchronizedLinkedHashMap",
                Collections.synchronizedMap(
                    new LinkedHashMap<Integer, String>(maxMapCapacity)));

        suite.addObjectToTest("ConcurrentHashMap",
                new ConcurrentHashMap<Integer, String>(maxMapCapacity));

        suite.addObjectToTest("SynchronizedHashMap",
                Collections.synchronizedMap(
                    new HashMap<Integer, String>(maxMapCapacity)));

        return suite;
    }

    private PerformanceConsumer createAssertSuite() {
        final AssertPerformanceForExecutionSuite suite =
                AssertPerformanceForExecutionSuite.withTolerance(
                    AssertPerformance.SUPER_SAFE_TOLERANCE);

        suite.forExecution("CONCURRENT RANDOM READ")
            .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");

        suite.forExecution("CONCURRENT RANDOM WRITE")
            .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");

        return suite;
    }

}
