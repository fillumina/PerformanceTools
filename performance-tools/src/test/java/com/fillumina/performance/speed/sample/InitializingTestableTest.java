package com.fillumina.performance.speed.sample;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.PerformanceTimerFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class InitializingTestableTest {

    @Test
    public void shouldInitializeTheTest() {
        final AtomicBoolean initialized = new AtomicBoolean(false);

        PerformanceTimerFactory
                .createSingleThreaded()

                .addTest("initialize", new Testable() {

                    @Override
                    public void setUp() {
                        initialized.set(true);
                    }

                    @Override
                    public void test() {
                    }
                })

                .execute(1);

        assertTrue(initialized.get());
    }

    @Test
    public void shouldTheInitializerNotBeCalledOnWarmupAgain() {
        final AtomicInteger counter = new AtomicInteger(0);

        final DefaultPerformanceTimer pt = PerformanceTimerFactory
                .createSingleThreaded()

                .addTest("initialize", new Testable() {

                    @Override
                    public void setUp() {
                        counter.getAndIncrement();
                    }

                    @Override
                    public void test() {
                    }
                });

        pt.warmup(1);
        assertEquals(1, counter.get());

        pt.execute(1);
        assertEquals(1, counter.get());
    }
}
