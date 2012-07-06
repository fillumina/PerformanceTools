package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerBuilder;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class InitializingRunnableTest {

    @Test
    public void shouldInitializeTheTest() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

        final AtomicBoolean initialized = new AtomicBoolean(false);

        pt.addTest("initialize", new InitializingRunnable() {

            @Override
            public void init() {
                initialized.set(true);
            }

            @Override
            public void run() {
                // do nothing
            }
        });

        pt.iterate(1);

        assertTrue(initialized.get());
    }
}
