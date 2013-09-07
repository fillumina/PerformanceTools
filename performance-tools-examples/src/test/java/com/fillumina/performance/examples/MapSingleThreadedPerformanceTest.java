package com.fillumina.performance.examples;

import com.fillumina.performance.producer.suite.ParametrizedRunnable;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MapSingleThreadedPerformanceTest {
    private static final int MAX_CAPACITY = 128;

    private ParametrizedPerformanceSuite<Map<Integer,String>> testSuite;

    public static void main(final String[] args) {
        new MapSingleThreadedPerformanceTest()
            .executeSuite(MAX_CAPACITY, StringTableViewer.CONSUMER);
    }

    @Before
    public void initTestSuite() {
        testSuite = executeSuite(MAX_CAPACITY,
                NullPerformanceConsumer.INSTANCE);
    }

    @Test
    public void shouldTreeMapBeSlowerThanHashMapOnSequentialRead() {
        testSuite.addPerformanceConsumer(new AssertPerformanceForExecutionSuite()
                .forExecution("SEQUENTIAL READ")
                    .assertTest("TreeMap").slowerThan("HashMap"));
    }

    public ParametrizedPerformanceSuite<Map<Integer,String>>
            executeSuite(
                final int maxCapacity,
                final PerformanceConsumer performanceConsumer) {

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
                createSingleThreadPerformanceSuite();

        addObjects(suite, maxCapacity);

        suite.addPerformanceConsumer(performanceConsumer);
        suite.addPerformanceConsumer(createAssertSuite());

        return executeTests(suite, maxCapacity);
    }

    private ParametrizedPerformanceSuite<Map<Integer,String>>
            createSingleThreadPerformanceSuite() {

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
            PerformanceTimerBuilder.createSingleThreaded()
                .instrumentedBy(AutoProgressionPerformanceInstrumenter
                        .builder()
                        .setSamplesPerMagnitude(15)
                        .setBaseIterations(1_000)
                        .setTimeout(240, TimeUnit.SECONDS)
                        .setMaxStandardDeviation(1.4)
                        .build())

                .instrumentedBy(new ParametrizedPerformanceSuite
                                        <Map<Integer,String>>());

        return suite;
    }

    private void addObjects(
            final ParametrizedPerformanceSuite<Map<Integer, String>> suite,
            final int maxCapacity) {

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
                Collections.synchronizedMap(
                    new HashMap<Integer, String>(maxCapacity)));
    }

    private ParametrizedPerformanceSuite<Map<Integer,String>> executeTests(
            final ParametrizedPerformanceSuite<Map<Integer, String>> suite,
            final int maxCapacity) {
        final int maxCapacityPlusOne = maxCapacity + 1;

        suite.executeTest("SEQUENTIAL READ", new FilledMapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % maxCapacityPlusOne, "xyz");
            }
        });

        suite.executeTest("SEQUENTIAL WRITE", new MapTest(maxCapacity) {

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.put(i % maxCapacityPlusOne, "xyz");
            }
        });

        suite.executeTest("RANDOM READ", new FilledMapTest(maxCapacity) {
            final Random rnd = new Random();

            @Override
            public void call(Map<Integer, String> map, int i) {
                map.get(rnd.nextInt(maxCapacityPlusOne));
            }
        });

        suite.executeTest("RANDOM WRITE",
                new ParametrizedRunnable<Map<Integer, String>>() {
            final Random rnd = new Random();

            @Override
            public void call(Map<Integer, String> map) {
                map.put(rnd.nextInt(maxCapacityPlusOne), "xyz");
            }
        });

        return suite;
    }

    // Tests can be asserted all at once or/and one by one
    private AssertPerformanceForExecutionSuite createAssertSuite() {
        final AssertPerformanceForExecutionSuite ap =
                new AssertPerformanceForExecutionSuite();

        ap.forExecution("SEQUENTIAL READ")
                .assertTest("TreeMap").slowerThan("HashMap");

        ap.forExecution("SEQUENTIAL WRITE")
                .assertTest("TreeMap").slowerThan("HashMap");

        ap.forExecution("RANDOM READ")
                .assertTest("TreeMap").slowerThan("HashMap");

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
            MapFillerHelper.fillUpMap(map, maxCapacity);
        }
    }
}
