package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.ComposedName;
import static org.junit.Assert.*;

/**
 * Consumer that asserts that the executed iterations comply with expected ones.
 *
 * @author Francesco Illuminati
 */
public class AssertIterationsPerformanceConsumer
        implements PerformanceConsumer<SpeedStats> {
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
    public void consume(final ComposedName message, final SpeedStats stats) {
        final long it = stats
                .getPerformances()
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
