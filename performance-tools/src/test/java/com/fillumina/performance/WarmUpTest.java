package com.fillumina.performance;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.util.ComposedName;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class WarmUpTest {
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

    private static class Statistics
            implements PerformanceConsumer<PerformanceSample> {

        private int iterations;

        @Override
        public void consume(final ComposedName message,
                final PerformanceSample loopPerformances) {
//            iterations += loopPerformances.getIterations();
        }

        public int getTotalIterations() {
            return iterations;
        }
    }

    @Ignore @Test
    public void shouldNotCalculateTheStatisticsOnWarmupSingleThread() {
//        final CounterTest counter = new CounterTest();
//        final Statistics statistics = new Statistics();
//
//        final DefaultPerformanceTimer pt =
//                PerformanceTimerFactory.createSingleThreaded()
//                    .addTest("", counter)
//                    .addPerformanceSampleConsumer(statistics)
//                    .warmup(WARMUP);
//
//        assertEquals(0, statistics.getTotalIterations());
//
//        pt.iterationTimeEstimator(ITERATIONS);
//
//        assertEquals(ITERATIONS, statistics.getTotalIterations());
    }

    @Ignore @Test
    public void shouldNotCalculateTheStatisticsOnWarmupMultiThread() {
//        final CounterTest counter = new CounterTest();
//        final Statistics statistics = new Statistics();
//
//        final DefaultPerformanceTimer pt =
//            PerformanceTimerFactory.getMultiThreadedBuilder()
//                    .setConcurrencyLevel(CONCURRENCY_LEVEL)
//                    .build()
//                .addTest("", counter)
//                .addPerformanceSampleConsumer(statistics)
//                .warmup(WARMUP);
//
//        assertEquals(0, statistics.getTotalIterations());
//
//        pt.iterationTimeEstimator(ITERATIONS);
//
//        assertEquals(ITERATIONS, statistics.getTotalIterations());
    }
}
