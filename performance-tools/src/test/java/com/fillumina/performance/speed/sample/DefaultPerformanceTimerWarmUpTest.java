package com.fillumina.performance.speed.sample;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.mock.CountingTestable;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class DefaultPerformanceTimerWarmUpTest {
    private static final int CONCURRENCY_LEVEL = 32;
    private static final int WARMUP = 23;
    private static final int ITERATIONS = 79;

    @Test
    public void shouldWarmUpSingleThreaded() {
        final CountingTestable counterTest = new CountingTestable();

        PerformanceTimerFactory.createSingleThreadedWithFractions(1)
            .addTest("", counterTest)
            .warmup(WARMUP)
            .iterate(ITERATIONS);

        assertEquals(WARMUP + ITERATIONS, counterTest.getCounter());
    }

    @Test
    public void shouldWarmUpMultiThreaded() {
        final CountingTestable counterTest = new CountingTestable();

        PerformanceTimerFactory.getMultiThreadedBuilder()
                .setConcurrencyLevel(CONCURRENCY_LEVEL)
                .buildMultiThreadPerformanceTimer()
            .addTest("", counterTest)
            .warmup(WARMUP)
            .iterate(ITERATIONS);

        assertEquals((WARMUP + ITERATIONS) * CONCURRENCY_LEVEL,
                counterTest.getCounter());
    }
}
