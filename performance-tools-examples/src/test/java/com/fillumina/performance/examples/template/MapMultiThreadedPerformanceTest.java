package com.fillumina.performance.examples.template;

import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParametrizedTestable;
import com.fillumina.performance.template.AutoParametrizedPerformanceTemplate;
import com.fillumina.performance.template.ParametrizedAssertion;
import com.fillumina.performance.template.TestConfiguration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MapMultiThreadedPerformanceTest
        extends AutoParametrizedPerformanceTemplate<Map<Integer, String>> {
    private static final int MAX_CAPACITY = 128;
    private static final int MASK = MAX_CAPACITY + 1;

    private PrintOut printOut = new PrintOut();

    public static void main(final String[] args) {
        final MapMultiThreadedPerformanceTest test =
                new MapMultiThreadedPerformanceTest();
        test.printOut = new PrintOut(true);
        test.executeWithMediumOutput();
    }

    @Test
    // TODO make it in the lib
    public void executeTest() {
        if (printOut.isPrintOut()) {
            executeWithFullOutput();
        } else {
            executeWithoutOutput();
        }
    }

    @Override
    public void config(TestConfiguration configuration) {
        configuration
            .setName("Map Multi Threaded")
            .performSpeedTest()
                .setConcurrencyLevel(Runtime.getRuntime().availableProcessors())
//                .setBaseIterations(1_000)
                .setMaxPercentageMargin(10)
                .setMinConfidence(0.4)
//                .setGetSamplesUntilTimeout(true)
                .setTimeoutSeconds(60);
    }

    @Override
    public void addParameters(
            final ParameterContainer<Map<Integer, String>> parameters) {
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
    public void addTests(
            TestContainer<ParametrizedTestable<Map<Integer, String>>> tests) {

        final int[] randomArray = createRandomIndexes(MAX_CAPACITY);

        tests.addTest("CONCURRENT RANDOM READ",
                new ParametrizedTestable<Map<Integer, String>>() {
            int counter = 0;

            @Override
            public void setUp(final Map<Integer, String> map) {
                fillUpMap(map, MAX_CAPACITY);
            }

            @Override
            public Object test(final Map<Integer, String> map) {
                counter = (counter + 1) & MASK;
                return map.get(randomArray[counter]);
            }
        });

        tests.addTest("CONCURRENT RANDOM WRITE",
                new ParametrizedTestable<Map<Integer, String>>() {
            int counter = 0;

            @Override
            public Object test(final Map<Integer, String> map) {
                counter = (counter + 1) & MASK;
                return map.put(randomArray[counter], "xyz");
            }
        });
    }

    private int[] createRandomIndexes(final int maxIndex) {
        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        final int[] randomArray = new int[maxIndex];
        for (int i=0; i<maxIndex; i++) {
            randomArray[i] = i;
        }
        for (int i=0; i<maxIndex; i++) {
            final int idx1 = rnd.nextInt(maxIndex);
            final int idx2 = rnd.nextInt(maxIndex);
            int tmp = randomArray[idx1];
            randomArray[idx1] = randomArray[idx2];
            randomArray[idx2] = tmp;
        }
        return randomArray;
    }

    @Override
    public void addAssertions(ParametrizedAssertion assertion) {
        assertion.speed()
            .forTest("CONCURRENT RANDOM READ",
                AssertSpeed.withTolerancePercentage(7)
                        .assertOrder("SynchronizedHashMap")
                        .greaterThan("ConcurrentHashMap"))

            .forTest("CONCURRENT RANDOM WRITE",
                AssertSpeed.withTolerancePercentage(7)
                        .assertOrder("SynchronizedHashMap")
                        .greaterThan("ConcurrentHashMap"));
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
