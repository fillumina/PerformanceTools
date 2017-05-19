package com.fillumina.performance.examples.template;

import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.suite.ParameterContainer;
import com.fillumina.performance.suite.ParameterizedTestable;
import com.fillumina.performance.template.ParameterizedMixedAssertion;
import com.fillumina.performance.template.ParameterizedPerformanceTemplate;
import com.fillumina.performance.template.Configuration;
import com.fillumina.performance.util.stats.Ratio;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MapMultiThreadedPerformanceTest
        extends ParameterizedPerformanceTemplate<Map<Integer, String>> {
    private static final int MAX_CAPACITY = 128;
    private static final int MASK = MAX_CAPACITY + 1;

    private static final String CONCURRENT_RANDOM_WRITE =
            "CONCURRENT RANDOM WRITE";
    private static final String CONCURRENT_RANDOM_READ =
            "CONCURRENT RANDOM READ";
    private static final String CONCURRENT_HASH_MAP = "ConcurrentHashMap";
    private static final String SYNCHRONIZED_HASH_MAP = "SynchronizedHashMap";

    private PrintOut printOut = new PrintOut();

    public static void main(final String[] args) {
        final MapMultiThreadedPerformanceTest test =
                new MapMultiThreadedPerformanceTest();
        test.printOut = new PrintOut(true);
        test.executeWithMediumOutput();
    }

    //TODO failed: Map Multi Threaded : CONCURRENT RANDOM WRITE 'SynchronizedHashMap' (722.0563 +/- 6.7179 (97 samples) ns) expected greater than 'ConcurrentHashMap' (746.7997 +/- 12.4453 (97 samples) ns)  with a tolerance of 7.000 %
    @Test
    public void executeTest() {
        if (printOut.isPrintOut()) {
            executeWithFullOutput();
        } else {
            executeWithoutOutput();
        }
    }

    @Override
    public void config(Configuration configuration) {
        configuration
            .setName("Map Multi Threaded")
            .speedTestOnly()
                .setConcurrencyLevel(Runtime.getRuntime().availableProcessors())
                .setMaxPercentageMargin(10);
    }

    @Override
    public void addParameters(
            final ParameterContainer<Map<Integer, String>> parameters) {
        parameters.addParameter("SynchronizedLinkedHashMap",
                Collections.synchronizedMap(
                    new LinkedHashMap<Integer, String>(MAX_CAPACITY)));

        parameters.addParameter(CONCURRENT_HASH_MAP,
                new ConcurrentHashMap<Integer, String>(MAX_CAPACITY));

        parameters.addParameter(SYNCHRONIZED_HASH_MAP,
                Collections.synchronizedMap(
                    new HashMap<Integer, String>(MAX_CAPACITY)));
    }

    @Override
    public void addTests(
            TestContainer<ParameterizedTestable<Map<Integer, String>>> tests) {

        final int[] randomArray = createRandomIndexes(MAX_CAPACITY);

        tests.addTest(CONCURRENT_RANDOM_READ,
                new ParameterizedTestable<Map<Integer, String>>() {
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

        tests.addTest(CONCURRENT_RANDOM_WRITE,
                new ParameterizedTestable<Map<Integer, String>>() {
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
    public void addAssertions(ParameterizedMixedAssertion assertion) {
        assertion.speed()
            .forTest(CONCURRENT_RANDOM_READ)
                .setTolerance(Ratio.percentage(7))
                    .assertOrder(SYNCHRONIZED_HASH_MAP)
                        .greaterThan(CONCURRENT_HASH_MAP)
                .end()
            .forTest(CONCURRENT_RANDOM_WRITE)
                .setTolerance(Ratio.percentage(7))
                    .assertOrder(SYNCHRONIZED_HASH_MAP)
                        .greaterThan(CONCURRENT_HASH_MAP);
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
