package com.fillumina.performance.template;

import com.fillumina.performance.consumer.assertion.PerformanceAssertion;
import com.fillumina.performance.producer.TestContainer;
import com.fillumina.performance.producer.timer.BulkTestable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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
    public void init(ProgressionConfigurator config) {
        config.setBaseIterations(100);
    }

    @Override
    public void addTests(TestContainer tests) {
        tests.addTest("HashMap", new AbstractMapBulkTestable() {
            @Override
            public Map<Integer,String> createTestObject() {
                return new HashMap<>();
            }
        });
        tests.addTest("LinkedHashMap", new AbstractMapBulkTestable() {
            @Override
            public Map<Integer,String> createTestObject() {
                return new LinkedHashMap<>();
            }
        });
    }

    @Override
    public void addAssertions(PerformanceAssertion assertion) {
    }

    private static abstract class AbstractMapBulkTestable extends
            BulkTestable<Map<Integer, String>, int[]> {
        @Override
        public int[] createTestValues() {
            final ThreadLocalRandom rnd = ThreadLocalRandom.current();
            int[] values = new int[32];
            values[0] = 12;
            for (int i=1; i<32; i++) {
                values[i] = 13 + rnd.nextInt(87);
            }
            return values;
        }

        @Override
        public void beforeTest(Map<Integer,String> map, int[] values) {
            for (int v : values) {
                map.put(v, ""+v);
            }
        }

        @Override
        public Object test(Map<Integer, String> map) {
            return map.remove(12);
        }

        @Override
        protected int getItems() {
            return 1_000;
        }
    }

}
