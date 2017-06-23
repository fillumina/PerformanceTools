package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.time.stats.progression.StatsProgressionStatusListener;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.TName;
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
    public void acceptStatsProgressionStatus(TName name,
            TimeStats stats,
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

    @Override
    public void acceptWarmupProgressionStatus(TName name, double speed) {
    }

}
