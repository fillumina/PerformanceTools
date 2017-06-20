package com.fillumina.performance.util.formatter;

import static com.fillumina.performance.util.formatter.PerformanceTimeHelper.*;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimeHelperTest {

    @BeforeClass
    public static void printoutMessage() {
        System.out.println("evaluating system timer precision...");
    }

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

    private void assertElapsedMillis(final int millis) {
        final int micro = millis * 1_000;
        final int iterations = (int) ((1E6/2) / micro);
        final long time = System.currentTimeMillis();
        for (int i=0; i<iterations; i++) {
            sleepMicroseconds(micro);
        }
        final long fullElapsed = System.currentTimeMillis() - time;
        final long elapsed = fullElapsed / iterations;
        final int tolerance = millis/10; // 10% tolerance
        final String msg =
                "total=" + fullElapsed +
                "\tmillis=" + millis +
                "\telapsed(ms)=" + elapsed;
        System.out.println(msg);
        assertEquals(msg, millis, elapsed, tolerance);
    }
}
