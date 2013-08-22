package com.fillumina.performance;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class WarmUpTest {
    private static final int CONCURRENCY_LEVEL = 32;
    private static final int WARMUP = 23;
    private static final int ITERATIONS = 79;

    private static class Counter implements Runnable {
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
        final Counter counter = new Counter();

        PerformanceTimerBuilder.createSingleThreaded()
            .addTest("", counter)
            .warmup(WARMUP)
            .iterate(ITERATIONS);

        assertEquals(WARMUP + ITERATIONS, counter.getValue());
    }

    @Test
    public void shouldWarmUpMultiThreaded() {
        final Counter counter = new Counter();

        PerformanceTimerBuilder.getMultiThreadedBuilder()
                .setConcurrencyLevel(CONCURRENCY_LEVEL)
                .buildPerformanceTimer()
            .addTest("", counter)
            .warmup(WARMUP)
            .iterate(ITERATIONS);

        assertEquals((WARMUP + ITERATIONS) * CONCURRENCY_LEVEL,
                counter.getValue());
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
        final Counter counter = new Counter();
        final Statistics statistics = new Statistics();

        final PerformanceTimer pt =
                PerformanceTimerBuilder.createSingleThreaded()
                    .addTest("", counter)
                    .addPerformanceConsumer(statistics)
                    .warmup(WARMUP);

        assertEquals(0, statistics.getTotalIterations());

        pt.iterate(ITERATIONS);

        assertEquals(ITERATIONS, statistics.getTotalIterations());
    }

    @Test
    public void shouldNotCalculateTheStatisticsOnWarmupMultiThread() {
        final Counter counter = new Counter();
        final Statistics statistics = new Statistics();

        final PerformanceTimer pt =
            PerformanceTimerBuilder.getMultiThreadedBuilder()
                    .setConcurrencyLevel(CONCURRENCY_LEVEL)
                    .buildPerformanceTimer()
                .addTest("", counter)
                .addPerformanceConsumer(statistics)
                .warmup(WARMUP);

        assertEquals(0, statistics.getTotalIterations());

        pt.iterate(ITERATIONS);

        assertEquals(ITERATIONS, statistics.getTotalIterations());
    }
}
