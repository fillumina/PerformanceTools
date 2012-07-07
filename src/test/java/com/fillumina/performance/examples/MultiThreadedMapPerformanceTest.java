package com.fillumina.performance.examples;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.sequence.ParametrizedPerformanceSuite;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.sequence.ThreadLocalParametrizedRunnable;
import com.fillumina.performance.consumer.assertion.AssertPerformancesForExecutionSuite;
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
    private static final int MAX_CAPACITY = 128;

    public static void main(final String[] args) {
        final MultiThreadedMapPerformanceTest test =
                new MultiThreadedMapPerformanceTest();
        test.execute(LOOPS, MAX_CAPACITY, new StringTableViewer());
    }

    @Test
    public void test() {
        execute(LOOPS, MAX_CAPACITY, createAssertSuite());
    }

    private PerformanceConsumer createAssertSuite() {
        final AssertPerformancesForExecutionSuite suite = new AssertPerformancesForExecutionSuite();

        suite.setPercentageTolerance(10); // super safe

        suite.forExecution("CONCURRENT RANDOM READ")
            .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap")
            .assertTest("SynchronizedHashMap").slowerThan("SynchronizedLinkedHashMap");

        suite.forExecution("CONCURRENT RANDOM WRITE")
            .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");

        return suite;
    }

    public void execute(final int loops, final int maxCapacity,
            final PerformanceConsumer performanceConsumer) {
        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
                createMultiThreadPerformanceSuite(maxCapacity);

        suite.setPerformanceConsumer(performanceConsumer);

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
    private static ParametrizedPerformanceSuite<Map<Integer,String>>
            createMultiThreadPerformanceSuite(final int maxCapacity) {

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
                new ParametrizedPerformanceSuite<>(PerformanceTimerBuilder.createMultiThread()
                    .setConcurrencyLevel(8)
                    .setWorkerNumber(8)
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
