package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerBuilder;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class InitializingRunnableTest {

    @Test
    public void shouldInitializeTheTest() {
        final AtomicBoolean initialized = new AtomicBoolean(false);

        PerformanceTimerBuilder
                .createSingleThread()

                .addTest("initialize", new InitializableRunnable() {

                    @Override
                    public void init() {
                        initialized.set(true);
                    }

                    @Override
                    public void run() {
                        // do nothing
                    }
                })

                .iterate(1);

        assertTrue(initialized.get());
    }
}
