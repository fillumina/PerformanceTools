package com.fillumina.performance.producer.sequence;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.AbstractPerformanceConsumer;
import com.fillumina.performance.producer.timer.PerformanceTimer;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class ProgressionSequenceTest {

    private static class AssertCounter
            extends AbstractPerformanceConsumer<AssertCounter> {
        private static final long serialVersionUID = 1L;

        private int[] iterations;
        private int maxSamples;
        private int samples;
        private int counter;

        public AssertCounter setIterations(int... iterations) {
            this.iterations = iterations;
            return this;
        }

        public AssertCounter setSamples(final int maxSamples) {
            this.maxSamples = maxSamples;
            return this;
        }

        @Override
        public void process() {
            assertEquals(iterations[counter],
                    getLoopPerformances().getIterations());
            samples++;
            if (samples == maxSamples) {
                counter++;
                samples = 0;
            }
        }

    }

    @Test
    public void shouldRunTheDeclaredIterations() {
        new ProgressionChecker()
                .setBaseTimes(10)
                .setMagnitude(3)
                .setSamples(10)
                .assertSamples(10, 100, 1000);

        new ProgressionChecker()
                .setBaseTimes(5)
                .setMagnitude(4)
                .setSamples(2)
                .assertSamples(5, 50, 500, 5000);

        new ProgressionChecker()
                .setBaseTimes(1)
                .setMagnitude(4)
                .setSamples(1)
                .assertSamples(1, 10, 100, 1000);

    }

    private static class ProgressionChecker {
        private int baseTimes;
        private int magnitude;
        private int samples;

        public ProgressionChecker setBaseTimes(int baseTimes) {
            this.baseTimes = baseTimes;
            return this;
        }

        public ProgressionChecker setMagnitude(int magnitude) {
            this.magnitude = magnitude;
            return this;
        }

        public ProgressionChecker setSamples(int samples) {
            this.samples = samples;
            return this;
        }

        private void assertSamples(final int... iterations) {
            final PerformanceTimer pt = PerformanceTimerBuilder.createSingleThread();

            final AtomicInteger counter = new AtomicInteger();

            pt.addTest("counter", new Runnable() {

                @Override
                public void run() {
                    counter.incrementAndGet();
                }
            });

            new ProgressionSequence(pt)
                    .setOnIterationPerformanceConsumer(
                        new AssertCounter().setIterations(iterations).setSamples(samples))
                    .setBaseTimes(baseTimes)
                    .setMaximumMagnitude(magnitude)
                    .setSamplePerMagnitude(samples)
                    .executeSequence();
        }
    }

}
