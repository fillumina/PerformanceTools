package com.fillumina.performance;

import com.fillumina.performance.infrastructure.Testable;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimerFactoryTest {
    private static final String SINGLE_THREADED = "SINGLE";
    private static final String MULTI_THREADED = "MULTI";

    @Test
    public void shouldEvaluateSingleThreadedTest() {
        final AtomicReference<String> check = new AtomicReference<>(null);

        PerformanceTimerFactory.createSingleThreaded()
                .addTest(SINGLE_THREADED, new Testable() {
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

        PerformanceTimerFactory.getMultiThreadedBuilder()
                .setThreads(4)
                .setWorkers(4)
                .build()
                .addTest(MULTI_THREADED, new Testable() {
                    @Override public void run() {
                        check.set(MULTI_THREADED);
                    }
                })
                .iterate(1);

        assertEquals(MULTI_THREADED, check.get());
    }
}