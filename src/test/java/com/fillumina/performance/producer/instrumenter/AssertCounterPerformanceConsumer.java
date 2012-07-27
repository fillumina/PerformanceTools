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
    private int maxSamples;
    private int samples;
    private int counter;

    public AssertCounterPerformanceConsumer setIterations(long... iterations) {
        this.iterations = iterations;
        return this;
    }

    public AssertCounterPerformanceConsumer setSamples(final int maxSamples) {
        this.maxSamples = maxSamples;
        return this;
    }

    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        assertEquals(iterations[counter], loopPerformances.getIterations());
        incrementCounter();
    }

    public void assertIterationsNumber(final int expected) {
        assertEquals("There were a different number of iterations than expected",
                expected, counter);
    }

    private void incrementCounter() {
        samples++;
        if (samples == maxSamples) {
            counter++;
            samples = 0;
        }
    }
}
