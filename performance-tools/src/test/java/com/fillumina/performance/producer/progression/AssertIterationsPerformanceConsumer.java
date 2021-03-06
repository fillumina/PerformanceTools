package com.fillumina.performance.producer.progression;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.LoopPerformances;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertIterationsPerformanceConsumer implements PerformanceConsumer {
    private static final long serialVersionUID = 1L;
    private long[] iterations;
    private int samples, samplesPerIteration;

    public AssertIterationsPerformanceConsumer setIterations(long... iterations) {
        this.iterations = iterations;
        return this;
    }

    public AssertIterationsPerformanceConsumer setSamplesPerIteration(
            final int samplesPerIteration) {
        this.samplesPerIteration = samplesPerIteration;
        return this;
    }

    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        assertEquals(iterations[Math.min(getIterationIndex(), iterations.length)],
                loopPerformances.getIterations());
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
