package com.fillumina.performance.mem;

import com.fillumina.performance.executor.stats.Stats;
import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ThreadAllocationComparisonTest {
    private static volatile Object escape;

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectTooFewRounds() {
        ThreadAllocationComparison.compare(() -> {}, () -> {}, 32);
    }

    @Test
    public void shouldCountTransientAllocations() {
        Assume.assumeTrue(ThreadAllocationComparison.isSupported());
        Stats stats = ThreadAllocationComparison.compare(
                () -> allocate(256), () -> { allocate(256); allocate(256); }, 33);
        assertTrue(stats.getMeasure("reference").getMean() > 0);
        assertTrue(stats.getMeasure("candidate").getMean() > 0);
        assertTrue("the allocated arrays must not remain retained", escape == null);
    }

    @Test
    public void shouldReportOneObservationPerVariantPerRound() {
        Assume.assumeTrue(ThreadAllocationComparison.isSupported());
        Stats stats = ThreadAllocationComparison.compare(
                () -> allocate(256), () -> allocate(256), 33);
        assertEquals(33, stats.getMeasure("reference").getCount());
        assertEquals(33, stats.getMeasure("candidate").getCount());
    }

    private static void allocate(int length) {
        escape = new byte[length];
        escape = null;
    }
}
