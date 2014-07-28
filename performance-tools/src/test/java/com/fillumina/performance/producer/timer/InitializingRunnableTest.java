package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati
 */
public class InitializingRunnableTest {

    @Test
    public void shouldInitializeTheTest() {
        final AtomicBoolean initialized = new AtomicBoolean(false);

        PerformanceTimerFactory
                .createSingleThreaded()

                .addTest("initialize", new InitializingRunnable() {

                    @Override
                    public void setUp() {
                        initialized.set(true);
                    }

                    @Override
                    public void run() {
                        // do nothing
                    }
                })

                .iterate(100);

        assertTrue(initialized.get());
    }

    @Test
    public void shouldTheInitializerBeCalledOnWarmupToo() {
        final AtomicInteger counter = new AtomicInteger(0);

        final PerformanceTimer pt = PerformanceTimerFactory
                .createSingleThreaded()

                .addTest("initialize", new InitializingRunnable() {

                    @Override
                    public void setUp() {
                        counter.getAndIncrement();
                    }

                    @Override
                    public void run() {
                        // do nothing
                    }
                });

        pt.warmup(100);
        assertEquals(1, counter.get());

        pt.iterate(100);
        assertEquals(2, counter.get());
    }
}
