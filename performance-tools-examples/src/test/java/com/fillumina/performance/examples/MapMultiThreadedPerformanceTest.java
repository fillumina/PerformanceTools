package com.fillumina.performance.examples;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.suite.ParametrizedPerformanceSuite;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.NullPerformanceConsumer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.producer.suite.ThreadLocalParametrizedRunnable;
import com.fillumina.performance.consumer.assertion.AssertPerformanceForExecutionSuite;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.progression.NullStandardDeviationConsumer;
import com.fillumina.performance.producer.progression.StandardDeviationConsumer;
import com.fillumina.performance.util.ConcurrencyHelper;
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
 * @author fra
 */
public class MapMultiThreadedPerformanceTest {
    private static final int MAX_MAP_CAPACITY = 128;

    public static void main(final String[] args) {

        final MapMultiThreadedPerformanceTest test =
                new MapMultiThreadedPerformanceTest();

        test.execute(MAX_MAP_CAPACITY,
                StringCsvViewer.CONSUMER,
                StringTableViewer.CONSUMER,
                new StandardDeviationConsumerImpl());
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

        final int concurrency = ConcurrencyHelper.getConcurrencyLevel();

        final ParametrizedPerformanceSuite<Map<Integer,String>> suite =
            PerformanceTimerBuilder.createMultiThread()
                    .setConcurrencyLevel(concurrency)
                    .setWorkerNumber(concurrency)
                    .setTimeout(20, TimeUnit.SECONDS)
                    .build()

//                PerformanceTimerBuilder.createSingleThread()
                .addPerformanceConsumer(iterationConsumer)

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder())
                    .setMaxStandardDeviation(20) // it's really a lot!
                    .setSamplesPerMagnitude(40)
                    .setBaseIterations(1_000)
                    .setTimeout(3, TimeUnit.MINUTES)
                    .build()
                    .addStandardDeviationConsumer(standardDeviationConsumer)

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

    public void setTests(final ParametrizedPerformanceSuite<Map<Integer, String>> suite,
            final int maxMapCapacity) {
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

    private static class StandardDeviationConsumerImpl
            implements StandardDeviationConsumer {

        @Override
        public void consume(double stdDev) {
            System.out.println("Standard Deviation: " + stdDev);
        }
    }

}
