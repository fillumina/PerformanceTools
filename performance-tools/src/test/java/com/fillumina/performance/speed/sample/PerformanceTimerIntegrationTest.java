package com.fillumina.performance.speed.sample;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.PerformanceTimerFactory;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimerIntegrationTest {
    private static final int ITERATIONS = 1000;

    @Test
    public void shouldExecuteTheSingleThreadTestTheGivenNumberOfIterations() {
        new PerformanceTimerIntegrationTest()
                .setIterations(ITERATIONS)
                .setExpectedCounter(ITERATIONS)
                .setPerformanceTimer(PerformanceTimerFactory.createSingleThreaded())
                .iterationAccuracyCheck();
    }

    @Test
    public void shouldExecuteTheMultiThreadTestTheGivenNumberOfIterations() {
        final int workers = 32;
        new PerformanceTimerIntegrationTest()
                .setIterations(ITERATIONS)
                .setExpectedCounter(ITERATIONS * workers)
                .setPerformanceTimer(PerformanceTimerFactory.getMultiThreadedBuilder()
                    .setWorkers(workers)
                    .build())
                .iterationAccuracyCheck();
    }

    private int iterations;
    private int expectedCounter;
    private DefaultPerformanceTimer performanceTimer;

    public PerformanceTimerIntegrationTest
            setExpectedCounter(int expectedCounter) {
        this.expectedCounter = expectedCounter;
        return this;
    }

    public PerformanceTimerIntegrationTest
            setIterations(int iterations) {
        this.iterations = iterations;
        return this;
    }

    public PerformanceTimerIntegrationTest
            setPerformanceTimer(DefaultPerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
        return this;
    }

    private void iterationAccuracyCheck() {
        assert expectedCounter > 0;
        assert iterations > 0;
        assert performanceTimer != null;

        final AtomicLong counter1 = new AtomicLong();
        final AtomicLong counter2 = new AtomicLong();

        performanceTimer.addTest("first", new Testable() {

            @Override
            public void test() {
                counter1.incrementAndGet();
            }
        })

        .addTest("second", new Testable() {

            @Override
            public void test() {
                counter2.incrementAndGet();
            }
        })

        .execute(iterations);

        assertEquals(expectedCounter, counter1.get());
        assertEquals(expectedCounter, counter2.get());
    }
}
