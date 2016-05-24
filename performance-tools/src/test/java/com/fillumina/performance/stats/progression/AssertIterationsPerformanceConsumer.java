package com.fillumina.performance.stats.progression;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.ComposedName;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertIterationsPerformanceConsumer
        implements PerformanceConsumer<PerformanceStats> {
    private int[] iterations;
    private int currentIteration;
    private int samplesPerIteration;

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
    public void consume(final ComposedName message, final PerformanceStats stats) {
        final long it = stats
                .getTestPerformances()
                .values()
                .iterator()
                .next()
                .getIterations();
        assertEquals(iterations[currentIteration] * samplesPerIteration, it);
        currentIteration++;
    }

    public void assertIterationsNumber(final int expected) {
        assertEquals("There were a different number of iterations than expected",
                expected, currentIteration);
    }
}
