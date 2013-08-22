package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerBuilder;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceTimerIterationsAccuracyTest {
    private static final int ITERATIONS = 1000;

    @Test
    public void shouldExecuteTheSingleThreadTestTheGivenNumberOfIterations() {
        new PerformanceTimerIterationsAccuracyTest()
                .setIterations(ITERATIONS)
                .setExpectedCounter(ITERATIONS)
                .setPerformanceTimer(PerformanceTimerBuilder.createSingleThreaded())
                .iterationAccuracyCheck();
    }

    @Test
    public void shouldExecuteTheMultiThreadTestTheGivenNumberOfIterations() {
        final int workers = 32;
        new PerformanceTimerIterationsAccuracyTest()
                .setIterations(ITERATIONS)
                .setExpectedCounter(ITERATIONS * workers)
                .setPerformanceTimer(PerformanceTimerBuilder.getMultiThreadedBuilder()
                    .setWorkers(workers)
                    .buildPerformanceTimer())
                .iterationAccuracyCheck();
    }

    private int iterations;
    private int expectedCounter;
    private PerformanceTimer performanceTimer;

    public PerformanceTimerIterationsAccuracyTest
            setExpectedCounter(int expectedCounter) {
        this.expectedCounter = expectedCounter;
        return this;
    }

    public PerformanceTimerIterationsAccuracyTest
            setIterations(int iterations) {
        this.iterations = iterations;
        return this;
    }

    public PerformanceTimerIterationsAccuracyTest
            setPerformanceTimer(PerformanceTimer performanceTimer) {
        this.performanceTimer = performanceTimer;
        return this;
    }

    private void iterationAccuracyCheck() {
        assert expectedCounter > 0;
        assert iterations > 0;
        assert performanceTimer != null;

        final AtomicLong counter1 = new AtomicLong();
        final AtomicLong counter2 = new AtomicLong();

        performanceTimer.addTest("first", new Runnable() {

            @Override
            public void run() {
                counter1.incrementAndGet();
            }
        })

        .addTest("second", new Runnable() {

            @Override
            public void run() {
                counter2.incrementAndGet();
            }
        })

        .iterate(iterations);

        assertEquals(expectedCounter, counter1.get());
        assertEquals(expectedCounter, counter2.get());
    }
}
