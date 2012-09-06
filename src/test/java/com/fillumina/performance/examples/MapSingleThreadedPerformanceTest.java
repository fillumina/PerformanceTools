package com.fillumina.performance.examples;

import com.fillumina.performance.producer.suite.ParametrizedRunnable;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.util.junit.JUnitPerformanceTestRunner;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author fra
 */
@RunWith(JUnitPerformanceTestRunner.class)
public class MapSingleThreadedPerformanceTest {
    private static final int ITERATIONS = 10_000_000;
    private static final int MAX_CAPACITY = 128;

    public static void main(final String[] args) {
        new MapSingleThreadedPerformanceTest()
            .execute(ITERATIONS, MAX_CAPACITY, StringTableViewer.CONSUMER);
    }

    @Test
    public void shouldPerformAssertions() {
        execute(ITERATIONS, MAX_CAPACITY, NullPerformanceConsumer.INSTANCE);
    }

    public void execute(final int iterations, final int maxCapacity,
            final PerformanceConsumer performanceConsumer) {

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
                createSingleThreadPerformanceSuite(iterations, maxCapacity);

        suite.addPerformanceConsumer(performanceConsumer);
        suite.addPerformanceConsumer(createAssertSuite());

        executeTests(suite, maxCapacity);
    }

    private void executeTests(
            final ParametrizedPerformanceSuite<Map<Integer, String>> suite,
            final int maxCapacity) {

        suite.executeTest("SEQUENTIAL READ", new FilledMapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % MAX_CAPACITY, "xyz");
            }
        });

        suite.executeTest("SEQUENTIAL WRITE", new MapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % MAX_CAPACITY, "xyz");
            }
        });

        suite.executeTest("RANDOM READ", new FilledMapTest(maxCapacity) {
            final Random rnd = new Random();

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.get(rnd.nextInt(MAX_CAPACITY));
            }
        });

        suite.executeTest("RANDOM WRITE",
                new ParametrizedRunnable<Map<Integer, String>>() {
            final Random rnd = new Random();

            @Override
            public void call(Map<Integer, String> map) {
                map.put(rnd.nextInt(MAX_CAPACITY), "xyz");
            }
        });
    }

    private ParametrizedPerformanceSuite<Map<Integer,String>>
            createSingleThreadPerformanceSuite(
                final int iterations, final int maxCapacity) {

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
                PerformanceTimerBuilder.createSingleThread()
                    .setIterations(iterations)
                    .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder())
                        .setTimeout(20, TimeUnit.SECONDS)
                        .setMaxStandardDeviation(1.4)
                        .build()

                    .instrumentedBy(new ParametrizedPerformanceSuite<Map<Integer,String>>());

        suite.addObjectToTest("HashMap",
                new HashMap<Integer, String>(maxCapacity));

        suite.addObjectToTest("TreeMap",
                new TreeMap<Integer, String>());

        suite.addObjectToTest("LinkedHashMap",
                new LinkedHashMap<Integer, String>(maxCapacity));

        suite.addObjectToTest("SynchronizedLinkedHashMap",
                Collections.synchronizedMap(
                    new LinkedHashMap<Integer, String>(maxCapacity)));

        suite.addObjectToTest("ConcurrentHashMap",
                new ConcurrentHashMap<Integer, String>(maxCapacity));

        suite.addObjectToTest("SynchronizedHashMap",
                Collections.synchronizedMap(new HashMap<Integer, String>(maxCapacity)));

        return suite;
    }

    private AssertPerformanceForExecutionSuite createAssertSuite() {
        final AssertPerformanceForExecutionSuite ap =
                new AssertPerformanceForExecutionSuite();

        ap.forExecution("SEQUENTIAL READ")
                .assertTest("TreeMap").slowerThan("HashMap");

        ap.forExecution("SEQUENTIAL WRITE")
                .assertTest("TreeMap").equalsTo("HashMap");

        ap.forExecution("RANDOM READ")
                .assertTest("TreeMap").fasterThan("HashMap");

        ap.forExecution("RANDOM WRITE")
                .assertTest("TreeMap").slowerThan("HashMap");

        return ap;
    }

    private static abstract class MapTest
            extends ParametrizedRunnable<Map<Integer, String>> {
        final int maxCapacity;
        int i=0;

        public MapTest(final int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        @Override
        public void call(final Map<Integer, String> param) {
            call(param, i++ % maxCapacity);
        }

        public abstract void call(final Map<Integer, String> param, final int i);
    }

    private static abstract class FilledMapTest extends MapTest {

        public FilledMapTest(int maxCapacity) {
            super(maxCapacity);
        }

        @Override
        public void setUp(final Map<Integer, String> map) {
            MapPerformanceApp.fillUpMap(map, maxCapacity);
        }
    }
}
