package com.fillumina.performance.examples.template;

import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.annotation.SetUp;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.stats.Ratio;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MapMultiThreadedPerformanceTest extends PerformanceTemplate {

    private static final int MAX_CAPACITY = 128;
    private static final int MASK = MAX_CAPACITY + 1;

    private static final String CONCURRENT_RANDOM_WRITE =
            "CONCURRENT RANDOM WRITE";
    private static final String CONCURRENT_RANDOM_READ =
            "CONCURRENT RANDOM READ";

    private static final String CONCURRENT_HASH_MAP = "ConcurrentHashMap";
    private static final String SYNCHRONIZED_HASH_MAP = "SynchronizedHashMap";
    private static final String SYNCHRONIZED_LINKED_HASH_MAP =
            "SynchronizedLinkedHashMap";

    private PrintOut printOut = new PrintOut();

    public static void main(final String[] args) {
        final MapMultiThreadedPerformanceTest test =
                new MapMultiThreadedPerformanceTest();
        test.printOut = new PrintOut(true);
        test.executeWithMediumOutput();
    }

    @Test
    public void executeTest() {
        if (printOut.isPrintOut()) {
            executeWithFullOutput();
        } else {
            executeWithoutOutput();
        }
    }

    @Override
    public void config(MixedConfigurationBuilder<?> configuration) {
        configuration
            .setName("Map Multi Threaded")
            .speedConfig()
                .setConcurrencyLevel(Runtime.getRuntime().availableProcessors())
                .setMaxPercentageMargin(Ratio.percentage(10));
    }

    private final int[] randomArray = createRandomIndexes(MAX_CAPACITY);

    @Override
    public void addTests(TestConfiguration<?> tests) {

        tests.addTest(CONCURRENT_RANDOM_READ, new Runnable() {
            int counter = 0;

            @Param
            private Map<Integer,String> map;

            @SetUp
            public void setUp() {
                fillUpMap(map, MAX_CAPACITY);
            }

            @Override
            public void run() {
                counter = (counter + 1) & MASK;
                Sink.drain(map.get(randomArray[counter]));
            }
        });

        tests.addTest(CONCURRENT_RANDOM_WRITE, new Runnable() {
            int counter = 0;

            @Param
            private Map<Integer,String> map;

            @Override
            public void run() {
                counter = (counter + 1) & MASK;
                Sink.drain(map.put(randomArray[counter], "xyz"));
            }
        });

        tests.parameters()
                .name("map")
                    .value(SYNCHRONIZED_LINKED_HASH_MAP,
                            Collections.synchronizedMap(
                                new LinkedHashMap<Integer, String>(MAX_CAPACITY)))
                    .value(CONCURRENT_HASH_MAP,
                            new ConcurrentHashMap<Integer, String>(MAX_CAPACITY))
                    .value(SYNCHRONIZED_HASH_MAP,
                            Collections.synchronizedMap(
                                new HashMap<Integer, String>(MAX_CAPACITY)))
                .end()
            .end();

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
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions.avgTime()
            .forTest(CONCURRENT_RANDOM_READ)
                .tolerance(Ratio.percentage(7))
                    .order(SYNCHRONIZED_HASH_MAP).greaterThan(CONCURRENT_HASH_MAP)
                    .order(SYNCHRONIZED_HASH_MAP).greaterThan(CONCURRENT_HASH_MAP)
                .end()
            .end();
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
