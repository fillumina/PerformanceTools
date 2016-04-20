package com.fillumina.performance.template;

import com.fillumina.performance.stats.assertion.PerformanceAssertion;
import com.fillumina.performance.stats.viewer.StringTableViewer;
import com.fillumina.performance.sample.BulkTestable;
import com.fillumina.performance.sample.TestContainer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IncrementalAccuracyPerformanceTemplateTest
        extends AutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new IncrementalAccuracyPerformanceTemplateTest()
                .executeWithIntermediateOutput();
    }

    @Override
    public void init(TestConfigurator config) {
        // TODO add a maximum number of cycles
        // TODO add warmup
        // TODO add a memory check
        config.setBaseIterations(40_000)
                .setIncrementSamples()
                .setFractions(1)
                .setGarbageCollectorMillis(100)
                .setMaxStandardDeviation(2)
                .setLoopPerformanceConsumer(StringTableViewer.INSTANCE)
                .setMessage("test")
                .setTimeout(120, TimeUnit.MINUTES);
    }

    @Override
    public void addTests(TestContainer tests) {
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
    public void addAssertions(PerformanceAssertion assertion) {
    }

    private static abstract class AbstractMapBulkTestable
            extends BulkTestable<Map<Integer, String>, int[]> {
        private static final int ELEMENT_TO_REMOVE = 107;

        @Override
        public int[] createTestValues() {
            int[] values = createRandomIntArray(8, 100);
            values[7] = ELEMENT_TO_REMOVE;
            return values;
        }

        private int[] createRandomIntArray(int size, int max) {
            Random rnd = ThreadLocalRandom.current();
            int[] values = new int[max];
            for (int i=0; i<max; i++) {
                values[i] = i;
            }
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

        @Override
        public void beforeSample(Map<Integer,String> map, int[] values) {
            if (map.isEmpty()) {
                fillMapWithValues(map, values);
            } else {
                map.put(ELEMENT_TO_REMOVE, ""+ELEMENT_TO_REMOVE);
            }
            assertMapSize(map, 8);
        }

        private void fillMapWithValues(Map<Integer, String> map, int[] values) {
            for (int v : values) {
                map.put(v, ""+v);
            }
        }

        private void assertMapSize(Map<Integer, String> map, final int size) {
            if (map.size() != size) {
                throw new AssertionError("map size differs from 7, " +
                        map.toString());
            }
        }

        @Override
        public Object test(Map<Integer, String> map) {
            return map.remove(ELEMENT_TO_REMOVE);
        }
    }

}
