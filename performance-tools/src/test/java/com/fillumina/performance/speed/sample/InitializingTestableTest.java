package com.fillumina.performance.speed.sample;

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

                .addTest("initialize", new AbstractTestable() {

                    @Override
                    public void setUp() {
                        initialized.set(true);
                    }

                    @Override
                    public Object test() {
                        return null;
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

                .addTest("initialize", new AbstractTestable() {

                    @Override
                    public void setUp() {
                        counter.getAndIncrement();
                    }

                    @Override
                    public Object test() {
                        return null;
                    }
                });

        pt.warmup(1);
        assertEquals(1, counter.get());

        pt.execute(1);
        assertEquals(1, counter.get());
    }
}
