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
        // TODO add intermediate results such as with parametrized...
                .executeWithIntermediateOutput();
    }

    @Override
    public void init(ProgressionConfigurator config) {
        // TODO add a maximum iterations
        config.setBaseIterations(1_000)
                .setMaxStandardDeviation(2);
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

    private static abstract class AbstractMapBulkTestable extends
            BulkTestable<Map<Integer, String>, int[]> {

        @Override
        public int[] createTestValues() {
            final ThreadLocalRandom rnd = ThreadLocalRandom.current();
            int[] values = new int[8];
            for (int i=0; i<7; i++) {
                values[i] = 16 + rnd.nextInt(86);
            }
            values[7] = 7;
            return values;
        }

        @Override
        public void beforeTest(Map<Integer,String> map, int[] values) {
            map.clear();
            for (int v : values) {
                map.put(v, ""+v);
            }
        }

        @Override
        public Object test(Map<Integer, String> map) {
            return map.remove(7);
        }
    }

}
