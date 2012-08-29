package com.fillumina.performance.util;

import org.junit.Test;
import static com.fillumina.performance.util.PerformanceTimeHelper.*;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class PerformanceTimeHelperTest {

    @Test
    public void shouldBePrecise() {
        assertElapsedMillis(10);
        assertElapsedMillis(20);
        assertElapsedMillis(100);
        assertElapsedMillis(200);
        assertElapsedMillis(500);
        assertElapsedMillis(1_000);
    }

    private void assertElapsedMillis(final int millis) {
        final long time = System.currentTimeMillis();
        sleepMicroseconds(millis * 1_000);
        final long elapsed = System.currentTimeMillis() - time;

        assertEquals(millis, elapsed, 0.1);
    }
}
