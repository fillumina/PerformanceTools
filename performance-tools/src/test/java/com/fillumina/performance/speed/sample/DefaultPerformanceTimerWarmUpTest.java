package com.fillumina.performance.speed.sample;

import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.PerformanceTimerFactory;
import java.util.concurrent.atomic.AtomicInteger;
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

    private static class CounterTest extends AbstractTestable {
        private AtomicInteger localCounter = new AtomicInteger(0);

        @Override
        public Object test() {
            return localCounter.incrementAndGet();
        }

        public int getValue() {
            return localCounter.get();
        }
    }

    @Test
    public void shouldWarmUpSingleThreaded() {
        final CounterTest counterTest = new CounterTest();

        PerformanceTimerFactory.createSingleThreaded()
            .addTest("", counterTest)
            .warmup(WARMUP)
            .execute(ITERATIONS);

        assertEquals(WARMUP + ITERATIONS, counterTest.getValue());
    }

    @Test
    public void shouldWarmUpMultiThreaded() {
        final CounterTest counterTest = new CounterTest();

        PerformanceTimerFactory.getMultiThreadedBuilder()
                .setConcurrencyLevel(CONCURRENCY_LEVEL)
                .build()
            .addTest("", counterTest)
            .warmup(WARMUP)
            .execute(ITERATIONS);

        assertEquals((WARMUP + ITERATIONS) * CONCURRENCY_LEVEL,
                counterTest.getValue());
    }
}
