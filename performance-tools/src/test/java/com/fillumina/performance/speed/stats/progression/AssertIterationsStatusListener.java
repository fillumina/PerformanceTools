package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.StaticPath;
import static org.junit.Assert.*;

/**
 * Consumer that asserts that the executed iterations comply with expected ones.
 *
 * @author Francesco Illuminati
 */
public class AssertIterationsStatusListener
        implements StatsProgressionStatusListener {
    private int[] iterations;
    private int currentIteration;
    private int samplesPerIteration;

    public AssertIterationsStatusListener setIterations(int... iterations) {
        this.iterations = iterations;
        return this;
    }

    public AssertIterationsStatusListener setSamplesPerIteration(
            final int samplesPerIteration) {
        this.samplesPerIteration = samplesPerIteration;
        return this;
    }


    @Override
    public void acceptStatsProgressionStatus(StaticPath name, SpeedStats stats,
            String rejectionMessage) {
        final long it = stats
                .getPerformanceMap()
                .values()
                .iterator()
                .next()
                .getTotalIterations();
        assertEquals(iterations[currentIteration] * samplesPerIteration, it);
        currentIteration++;
    }

    public void assertIterationsNumber(final int expected) {
        assertEquals("There were a different number of iterations than expected",
                expected, currentIteration);
    }
}
