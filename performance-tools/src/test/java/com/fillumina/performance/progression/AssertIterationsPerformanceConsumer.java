package com.fillumina.performance.progression;

import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertIterationsPerformanceConsumer
        implements PerformanceStatsConsumer {
    private int[] iterations;
    private int samples, samplesPerIteration;

    public AssertIterationsPerformanceConsumer setIterations(int... iterations) {
        this.iterations = iterations;
        return this;
    }

    public AssertIterationsPerformanceConsumer setSamplesPerIteration(
            final int samplesPerIteration) {
        this.samplesPerIteration = samplesPerIteration;
        return this;
    }

    @Override
    public void consume(final String message, final PerformanceStats stats) {
        final long it = stats
                .getTestPerformances()
                .values()
                .iterator()
                .next()
                .getIterations();
        assertEquals(iterations[Math.min(getIterationIndex(), iterations.length)], it);
        samples++;
    }

    public void assertIterationsNumber(final int expected) {
        assertEquals("There were a different number of iterations than expected",
                expected, getIterationIndex());
    }

    private int getIterationIndex() {
        return samples / samplesPerIteration;
    }
}
