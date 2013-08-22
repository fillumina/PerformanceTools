package com.fillumina.performance;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class PerformanceTimerBuilderTest {
    private static final String SINGLE_THREADED = "SINGLE";
    private static final String MULTI_THREADED = "MULTI";

    @Test
    public void shouldEvaluateSingleThreadedTest() {
        final AtomicReference<String> check = new AtomicReference<>(null);

        PerformanceTimerBuilder.createSingleThreaded()
                .addTest(SINGLE_THREADED, new Runnable() {
                    @Override public void run() {
                        check.set(SINGLE_THREADED);
                    }
                })
                .iterate(1);

        assertEquals(SINGLE_THREADED, check.get());
    }

    @Test
    public void shouldEvaluateMultiThreadedTest() {
        final AtomicReference<String> check = new AtomicReference<>(null);

        PerformanceTimerBuilder.getMultiThreadedBuilder()
                .setThreads(4)
                .setWorkers(4)
                .buildPerformanceTimer()
                .addTest(MULTI_THREADED, new Runnable() {
                    @Override public void run() {
                        check.set(MULTI_THREADED);
                    }
                })
                .iterate(1);

        assertEquals(MULTI_THREADED, check.get());
    }
}