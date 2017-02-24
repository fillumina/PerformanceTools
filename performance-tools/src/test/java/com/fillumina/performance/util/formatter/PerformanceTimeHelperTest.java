package com.fillumina.performance.util.formatter;

import static com.fillumina.performance.util.formatter.PerformanceTimeHelper.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimeHelperTest {

    @Test
    public void shouldBePrecise_10() {
        assertElapsedMillis(10);
    }

    @Test
    public void shouldBePrecise_20() {
        assertElapsedMillis(20);
    }

    @Test
    public void shouldBePrecise_30() {
        assertElapsedMillis(30);
    }

    @Test
    public void shouldBePrecise_40() {
        assertElapsedMillis(40);
    }

    @Test
    public void shouldBePrecise_50() {
        assertElapsedMillis(50);
    }

    @Test
    public void shouldBePrecise_60() {
        assertElapsedMillis(60);
    }

    @Test
    public void shouldBePrecise_70() {
        assertElapsedMillis(70);
    }

    @Test
    public void shouldBePrecise_80() {
        assertElapsedMillis(80);
    }

    @Test
    public void shouldBePrecise_90() {
        assertElapsedMillis(90);
    }

    @Test
    public void shouldBePrecise_100() {
        assertElapsedMillis(100);
    }

    @Test
    public void shouldBePrecise_150() {
        assertElapsedMillis(150);
    }

    @Test
    public void shouldBePrecise_200() {
        assertElapsedMillis(200);
    }

    @Test
    public void shouldBePrecise_250() {
        assertElapsedMillis(250);
    }

    @Test
    public void shouldBePrecise_300() {
        assertElapsedMillis(300);
    }

    @Test
    public void shouldBePrecise_400() {
        assertElapsedMillis(400);
    }

    @Test
    public void shouldBePrecise_500() {
        assertElapsedMillis(500);
    }

    private void assertElapsedMillis(final int millis) {
        final long time = System.currentTimeMillis();
        sleepMicroseconds(millis * 1_000);
        final long elapsed = System.currentTimeMillis() - time;

        final int tolerance = millis/10;
        final String msg = "millis=" + millis + "\telapsed(ms)=" + elapsed;
        //System.out.println(msg);
        assertEquals(msg, millis, elapsed, tolerance);
    }
}
