package com.fillumina.performance.examples;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.BulkRunnable;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.stats.Ratio;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

/**
 * Tests the removal time of an element from an map. It's tricky to test
 * because once you remove an element the map changes and the next
 * iteration will non measure the same operation anymore. Even more to
 * perform the required number of removal needed to have a significant sample
 * time it means the map should be huge in size. These might not be what
 * really you need.<br>
 * To accomplish a repeatable accurate test you can remove the same element from
 * an array of identical maps all initialized the same and here is how you can
 * do that.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class BulkPerformanceTemplateTest
        extends PerformanceTemplate {

    public static void main(final String[] args) {
        new BulkPerformanceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void executeTest() {
        executeWithoutOutput();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> configuration) {
        configuration
                .setName(getClass().getSimpleName())
                .speedConfig()
                    .setMillisecondsPerSample(50)
                    .setMaxPercentageMargin(Ratio.percentage(7));
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("HashMap", new AbstractMapBulkTestable() {
            @Override
            public Map<Integer,String> createTestObject() {
                return new HashMap<>(16);
            }
        });
        tests.addTest("LinkedHashMap", new AbstractMapBulkTestable() {
            @Override
            public Map<Integer,String> createTestObject() {
                return new LinkedHashMap<>(16);
            }
        });
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertion) {
    }

    private static abstract class AbstractMapBulkTestable
            extends BulkRunnable<Map<Integer, String>> {
        private static final int ELEMENT_TO_REMOVE = 107;
        private static final String ELEMENT_TO_REMOVE_STR = ""+ELEMENT_TO_REMOVE;
        private int[] values;

        AbstractMapBulkTestable() {
            values = createRandomIntArray(8, 100);
            values[7] = ELEMENT_TO_REMOVE;
        }

        @Override
        public void onBeforeSample(Map<Integer,String> map) {
            if (map.isEmpty()) {
                for (int v : values) {
                    map.put(v, ""+v);
                }
            } else {
                map.put(ELEMENT_TO_REMOVE, ELEMENT_TO_REMOVE_STR);
            }
            assertMapSize(map, 8);
        }

        @Override
        public void test(Map<Integer, String> map) {
            Sink.drain(map.remove(ELEMENT_TO_REMOVE));
        }
    }

    private static void assertMapSize(Map<Integer, String> map, final int size) {
        if (map.size() != size) {
            throw new AssertionError(
                    "map size differs from " + size + ", " + map.toString());
        }
    }

    private static int[] createRandomIntArray(int size, int max) {
        Random rnd = ThreadLocalRandom.current();
        int[] values = new int[max];
        for (int i=0; i<max; i++) {
            values[i] = i;
        }
        // randomize the array
        int a, b, t;
        for (int i=0; i<max; i++) {
            a = rnd.nextInt(max);
            b = rnd.nextInt(max);
            t = values[a];
            values[a] = values[b];
            values[b] = t;
        }
        int[] result = new int[size];
        System.arraycopy(values, 0, result, 0, size);
        return result;
    }
}
