package com.fillumina.performance.examples;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.suite.ThreadLocalParametrizedRunnable;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.progression.NullStandardDeviationConsumer;
import com.fillumina.performance.producer.progression.StandardDeviationConsumer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * This test may take up to 5 minutes and more because the performances
 * in a multi threaded environment can be really unstable especially
 * for a not multi threaded algorithm.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MapMultiThreadedPerformanceTest {
    private static final int MAX_MAP_CAPACITY = 128;
    private static final String CONCURRENT_RANDOM_READ = "CONCURRENT RANDOM READ";
    private static final String CONCURRENT_RANDOM_WRITE = "CONCURRENT RANDOM WRITE";
    private static final String SYNCHRONIZED_HASH_MAP = "SynchronizedHashMap";
    private static final String CONCURRENT_HASH_MAP = "ConcurrentHashMap";
    private static final String SYNCHRONIZED_LINKED_HASH_MAP = "SynchronizedLinkedHashMap";

    public static void main(final String[] args) {

        final MapMultiThreadedPerformanceTest test =
                new MapMultiThreadedPerformanceTest();

        test.execute(MAX_MAP_CAPACITY,
                StringCsvViewer.INSTANCE,
                StringTableViewer.INSTANCE,
                PrintOutStandardDeviationConsumer.INSTANCE);
    }

    @Test
    public void test() {
        execute(MAX_MAP_CAPACITY,
                NullPerformanceConsumer.INSTANCE,
                NullPerformanceConsumer.INSTANCE,
                NullStandardDeviationConsumer.INSTANCE);
    }

    public void execute(final int maxMapCapacity,
            final PerformanceConsumer iterationConsumer,
            final PerformanceConsumer resultConsumer,
            final StandardDeviationConsumer standardDeviationConsumer) {

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
                createMultiThreadPerformanceSuite(maxMapCapacity,
                    iterationConsumer,
                    standardDeviationConsumer);

        suite.addPerformanceConsumer(resultConsumer);
        suite.addPerformanceConsumer(createAssertSuite());

        setTests(suite, maxMapCapacity);
    }

    @SuppressWarnings("unchecked")
    private static ParametrizedPerformanceSuite<Map<Integer,String>>
            createMultiThreadPerformanceSuite(final int maxMapCapacity,
            final PerformanceConsumer iterationConsumer,
            final StandardDeviationConsumer standardDeviationConsumer) {

        final int concurrency = 32;

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
            PerformanceTimerFactory.getMultiThreadedBuilder()
                    .setThreads(concurrency)
                    .setWorkers(concurrency)
                    .setTimeout(20, TimeUnit.SECONDS)
                    .buildPerformanceTimer()

//                PerformanceTimerFactory.createSingleThreaded()
                .addPerformanceConsumer(iterationConsumer)

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setMaxStandardDeviation(20) // it's really a lot!
                        .setSamplesPerStep(15)
                        .setBaseIterations(1_000)
                        .setTimeout(3, TimeUnit.MINUTES)
                        .build())

                .addStandardDeviationConsumer(standardDeviationConsumer)

                .instrumentedBy(new ParametrizedPerformanceSuite<Map<Integer,String>>());

        suite.addParameter(SYNCHRONIZED_LINKED_HASH_MAP,
                Collections.synchronizedMap(
                    new LinkedHashMap<Integer, String>(maxMapCapacity)));

        suite.addParameter(CONCURRENT_HASH_MAP,
                new ConcurrentHashMap<Integer, String>(maxMapCapacity));

        suite.addParameter(SYNCHRONIZED_HASH_MAP,
                Collections.synchronizedMap(
                    new HashMap<Integer, String>(maxMapCapacity)));

        return suite;
    }

    private PerformanceConsumer createAssertSuite() {
        final AssertPerformanceForExecutionSuite assertion =
                (AssertPerformanceForExecutionSuite)
                AssertPerformanceForExecutionSuite.withTolerance(
                    AssertPerformance.SUPER_SAFE_TOLERANCE);

        assertion.forExecution(CONCURRENT_RANDOM_READ)
            .assertTest(SYNCHRONIZED_HASH_MAP).slowerThan(CONCURRENT_HASH_MAP);

        assertion.forExecution(CONCURRENT_RANDOM_WRITE)
            .assertTest(SYNCHRONIZED_HASH_MAP).slowerThan(CONCURRENT_HASH_MAP);

        return assertion;
    }

    public void setTests(final ParametrizedPerformanceSuite<Map<Integer, String>> suite,
            final int maxMapCapacity) {
        suite.executeTest(CONCURRENT_RANDOM_READ,
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

        suite.executeTest(CONCURRENT_RANDOM_WRITE,
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

}
