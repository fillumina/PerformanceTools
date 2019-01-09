package com.fillumina.performance.integration;

import com.fillumina.performance.executor.annotation.BeforeSample;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

/**
 * Use a parameter to give the same test two different kind of {@link Map}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiThreadedPerformanceTemplateTest extends PerformanceTemplate {
    private static final String CONCURRENT_HASH_MAP = "ConcurrentHashMap";
    private static final String SYNCHRONIZED_HASH_MAP = "SynchronizedHashMap";
    private final int SIZE = 1_000;

    public static void main(final String[] args) {
        new MultiThreadedPerformanceTemplateTest()
                .executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.speedConfig()
                .setMaxPercentageMargin(Ratio.percentage(10))
                .setMultiThreading(true);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("test", new Runnable() {

            @Param
            private Map<Integer, Integer> map;

            @BeforeSample
            public void onBeforeSample() {
                // fill in the map with data
                for (int i=0; i<SIZE; i++) {
                    map.put(i, i);
                }
            }

            @Override
            public void run() {
                int index = ThreadLocalRandom.current().nextInt(SIZE);
                Sink.drain(map.get(index));
            }
        })
        .parameters()
                .name("map")
                    .value(CONCURRENT_HASH_MAP,
                            new ConcurrentHashMap<Integer, Integer>())
                    .value(SYNCHRONIZED_HASH_MAP,
                            Collections.synchronizedMap(
                                    new HashMap<Integer,Integer>()))
                    .end();
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions
            .throughput()
                .forTest("test")
                .percentage(CONCURRENT_HASH_MAP)
                    .greaterThan(Ratio.percentage(50))
            .end()
            .avgTime()
                .forTest("test")
                .order(CONCURRENT_HASH_MAP)
                    .lessThan(SYNCHRONIZED_HASH_MAP)
            .end();
    }

}
