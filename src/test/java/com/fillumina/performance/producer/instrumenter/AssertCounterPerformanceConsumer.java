package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class AssertCounterPerformanceConsumer implements PerformanceConsumer {
    private static final long serialVersionUID = 1L;
    private long[] iterations;
    private int samples, samplesPerIteration;

    public AssertCounterPerformanceConsumer setIterations(long... iterations) {
        this.iterations = iterations;
        return this;
    }

    public AssertCounterPerformanceConsumer setSamplesPerIteration(
            final int samplesPerIteration) {
        this.samplesPerIteration = samplesPerIteration;
        return this;
    }

    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        assertEquals(iterations[getIterationIndex()],
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
