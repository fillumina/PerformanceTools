package com.fillumina.performance.executor.test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class BulkRunnableTest {
    private static final String TEST_VALUE = "test value";

    private static class BulkRunnableImpl
            extends BulkRunnable<List<String>> {
        private int counter;

        @Override
        public List<String> createTestObject() {
            return new ArrayList<>();
        }

        @Override
        public void onBeforeSample(List<String> list) {
            list.add(TEST_VALUE);
        }

        @Override
        public void test(List<String> list) {
            counter++;
            assertEquals(1, list.size());
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
