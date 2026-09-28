package com.fillumina.performance.examples.template;

import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.annotation.SetUp;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.stats.Ratio;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

/**
 * Compares map reads and writes. Only lookup order is a gate; write order
 * depends on the JVM and workload.
 *
 * @author Francesco Illuminati
 */
public class MapSingleThreadedPerformanceTest extends PerformanceTemplate {
    private static final String RANDOM_WRITE = "RANDOM WRITE";
    private static final String RANDOM_READ = "RANDOM READ";
    private static final String INDEXED_HASH_MAP = "IndexedHashMap";
    private static final String HASH_MAP = "HashMap";

    private PrintOut printOut = new PrintOut();

    public static void main(final String[] args) {
        final MapSingleThreadedPerformanceTest test =
                new MapSingleThreadedPerformanceTest();
        test.printOut = new PrintOut(true);
        test.executeWithFullOutput();
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
            .setName("map single threaded")
                .speedConfig()
                    .setFixedSamples(10);
    }


    @Override
    public void addTests(TestConfiguration<?> tests) {

        tests.addTest(RANDOM_READ, new Runnable() {
            @Param
            private Map<Integer, String> map;

            @Sequence
            private int capacity;

            @SetUp
            public void setUp() {
                initMap(map, capacity);
            }

            @Override
            public void run() {
                final int idx = ThreadLocalRandom.current().nextInt(capacity);
                Sink.drain(map.get(idx));
            }
        });

        tests.addTest(RANDOM_WRITE, new Runnable() {
            @Param
            private Map<Integer, String> map;

            @Sequence
            private int capacity;

            @SetUp
            public void setUp() {
                initMap(map, capacity);
            }

            @Override
            public void run() {
                final int idx = ThreadLocalRandom.current().nextInt(capacity);
                Sink.drain(map.put(idx, "xyz"));
            }
        });

        tests.parameters()
                .name("map")
                    .value(HASH_MAP, new HashMap<Integer, String>())
                    .value(INDEXED_HASH_MAP, new IndexedHashMap<Integer, String>())
                .end()
            .end()
            .sequences()
                .name("capacity").values(50).end()
            .end();

    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        final Ratio tolerance = Ratio.percentage(5);
        assertions.avgTime()
            .tolerance(tolerance)
                .forTest("capacity_50", RANDOM_READ).order(INDEXED_HASH_MAP).greaterThan(HASH_MAP)
            .end();

    }

    private static void initMap(Map<Integer,String> map, int capacity) {
        map.clear();
        final List<Integer> list = new ArrayList<>(capacity);
        for (int i=0; i<capacity; i++) {
            list.add(i);
        }
        Collections.shuffle(list, new Random(System.currentTimeMillis()));
        for (int i=0; i<capacity; i++) {
            map.put(list.get(i), "xyz");
        }
    }

}
