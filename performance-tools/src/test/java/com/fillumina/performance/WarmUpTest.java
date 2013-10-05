package com.fillumina.performance;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class WarmUpTest {
    private static final int CONCURRENCY_LEVEL = 32;
    private static final int WARMUP = 23;
    private static final int ITERATIONS = 79;

    private static class CounterTest implements Runnable {
        private AtomicInteger localCounter = new AtomicInteger(0);

        @Override
        public void run() {
            localCounter.incrementAndGet();
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
            .iterate(ITERATIONS);

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
            .iterate(ITERATIONS);

        assertEquals((WARMUP + ITERATIONS) * CONCURRENCY_LEVEL,
                counterTest.getValue());
    }

    private static class Statistics implements PerformanceConsumer {

        private int iterations;

        @Override
        public void consume(final String message,
                final LoopPerformances loopPerformances) {
            iterations += loopPerformances.getIterations();
        }

        public int getTotalIterations() {
            return iterations;
        }
    }

    @Test
    public void shouldNotCalculateTheStatisticsOnWarmupSingleThread() {
        final CounterTest counter = new CounterTest();
        final Statistics statistics = new Statistics();

        final PerformanceTimer pt =
                PerformanceTimerFactory.createSingleThreaded()
                    .addTest("", counter)
                    .addPerformanceConsumer(statistics)
                    .warmup(WARMUP);

        assertEquals(0, statistics.getTotalIterations());

        pt.iterate(ITERATIONS);

        assertEquals(ITERATIONS, statistics.getTotalIterations());
    }

    @Test
    public void shouldNotCalculateTheStatisticsOnWarmupMultiThread() {
        final CounterTest counter = new CounterTest();
        final Statistics statistics = new Statistics();

        final PerformanceTimer pt =
            PerformanceTimerFactory.getMultiThreadedBuilder()
                    .setConcurrencyLevel(CONCURRENCY_LEVEL)
                    .build()
                .addTest("", counter)
                .addPerformanceConsumer(statistics)
                .warmup(WARMUP);

        assertEquals(0, statistics.getTotalIterations());

        pt.iterate(ITERATIONS);

        assertEquals(ITERATIONS, statistics.getTotalIterations());
    }
}
