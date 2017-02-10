package com.fillumina.performance.template.speed;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.BulkTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

/**
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
    public void config(TestConfiguration configuration) {
        configuration
                .setName("BulkPerformanceTemplateTest")
                .speedTestOnly()
                    .setBulkSpecificConfig()
                    .setMaxPercentageMargin(7);
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
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
    public void addAssertions(ProgressionAssertion assertion) {
    }

    private static abstract class AbstractMapBulkTestable
            extends BulkTestable<Map<Integer, String>, int[]> {
        private static final int ELEMENT_TO_REMOVE = 107;
        private static final String ELEMENT_TO_REMOVE_STR = ""+ELEMENT_TO_REMOVE;

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

        @Override
        public void beforeSample(Map<Integer,String> map, int[] values) {
            if (map.isEmpty()) {
                fillMapWithValues(map, values);
            } else {
                map.put(ELEMENT_TO_REMOVE, ELEMENT_TO_REMOVE_STR);
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
                throw new AssertionError("map size differs from " + size + ", " +
                        map.toString());
            }
        }

        @Override
        public Object test(Map<Integer, String> map) {
            return map.remove(ELEMENT_TO_REMOVE);
        }
    }

}
