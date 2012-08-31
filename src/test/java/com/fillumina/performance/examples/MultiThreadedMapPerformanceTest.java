package com.fillumina.performance.examples;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.suite.ThreadLocalParametrizedRunnable;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author fra
 */
public class MultiThreadedMapPerformanceTest {
    private static final int LOOPS = 1_000_000;
    private static final int MAX_MAP_CAPACITY = 128;

    public static void main(final String[] args) {
        final MultiThreadedMapPerformanceTest test =
                new MultiThreadedMapPerformanceTest();
        test.execute(LOOPS, MAX_MAP_CAPACITY, StringTableViewer.CONSUMER);
    }

    @Test
    public void test() {
        execute(LOOPS, MAX_MAP_CAPACITY, createAssertSuite());
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

    public void execute(final int loops, final int maxMapCapacity,
            final PerformanceConsumer performanceConsumer) {

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
                createMultiThreadPerformanceSuite(maxMapCapacity);

        suite.addPerformanceConsumer(performanceConsumer);

        suite.executeTest("CONCURRENT RANDOM READ", loops,
                new ThreadLocalParametrizedRunnable<Random, Map<Integer, String>>() {

            @Override
            public void setUp(final Map<Integer, String> map) {
                MapPerformanceApp.fillUpMap(map, maxMapCapacity);
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

        suite.executeTest("CONCURRENT RANDOM WRITE", loops,
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
                new ParametrizedPerformanceSuite<>(
                    PerformanceTimerBuilder.createMultiThread()
                        .setConcurrencyLevel(8)
                        .setWorkerNumber(8)
                        .setTimeout(25, TimeUnit.SECONDS)
                        .build());

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

}
