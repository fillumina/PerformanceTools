package com.fillumina.performance.speed.stats.instrumenter;

import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.StaticPath;
import static org.junit.Assert.*;

/**
 * Consumer that asserts that the executed iterations comply with expected ones.
 *
 * @author Francesco Illuminati
 */
class AssertIterationsStatusListener
        implements StatsProgressionStatusListener {
    private int[] iterations;
    private int currentIteration;
    private int samples;

    public AssertIterationsStatusListener setIterations(int... iterations) {
        this.iterations = iterations;
        return this;
    }

    public AssertIterationsStatusListener setSamples(final int samples) {
        this.samples = samples;
        return this;
    }

    @Override
    public void acceptStatsProgressionStatus(StaticPath name,
            SpeedStats stats,
            String rejectionMessage) {
        final long it = stats
                .getSingleStatsMap()
                .values()
                .iterator()
                .next()
                .getTotalIterations();
        assertEquals(iterations[currentIteration] * samples, it);
        currentIteration++;
    }

    public void assertIterationsNumber(final int expected) {
        assertEquals("There is a different number of iterations than expected",
                expected, currentIteration);
    }
}
