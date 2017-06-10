package com.fillumina.performance.infrastructure;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class BulkRunnableTest {
    private static final String TEST_VALUE = "test value";

    private static class BulkRunnableImpl
            extends BulkRunnable<List<String>, String> {
        private int counter;

        @Override
        public List<String> createTestObject() {
            return new ArrayList<>();
        }

        @Override
        public String createTestValue() {
            return TEST_VALUE;
        }

        @Override
        public void onBeforeSample(List<String> list, String v) {
            assertTrue(list.isEmpty());
            list.add(v);
        }

        @Override
        public void test(List<String> list) {
            counter++;
            assertFalse(list.isEmpty());
        }
    }

    @Test
    public void shouldApplyTestOnDifferentObjects() {
        BulkRunnableImpl bulkRunnable = new BulkRunnableImpl();
        int samples = 10;

        bulkRunnable.onBeforeSample(samples);

        for (int i=0; i<samples; i++) {
            bulkRunnable.run();
        }

        assertEquals(samples, bulkRunnable.counter);
    }

    @Test
    public void shouldRecreateTheObjectsOnEverySample() {
        BulkRunnableImpl bulkRunnable = new BulkRunnableImpl();
        int samples = 10;

        bulkRunnable.onBeforeSample(samples);
        for (int i=0; i<samples; i++) {
            bulkRunnable.run();
        }

        bulkRunnable.onBeforeSample(samples);
        for (int i=0; i<samples; i++) {
            bulkRunnable.run();
        }

        assertEquals(samples * 2, bulkRunnable.counter);
    }
}
