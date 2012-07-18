package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.producer.timer.LoopPerformances;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class ProgressionPerformanceInstrumenterTest {

    @Test
    public void shouldRunTheDeclaredIterationsDefinedUsingBaseAndMagnitude() {
        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(10)
                .setMagnitude(3)
                .setSamples(10)
                .assertSamples(10, 100, 1000);

        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(5)
                .setMagnitude(4)
                .setSamples(2)
                .assertSamples(5, 50, 500, 5000);

        new BaseMagnitudeProgressionChecker()
                .setBaseTimes(1)
                .setMagnitude(4)
                .setSamples(1)
                .assertSamples(1, 10, 100, 1000);

    }

    @Test
    public void shouldRunTheDeclaredIterationsDefinedUsingIterations() {
        new IterationsProgressionChecker()
                .setSamples(10)
                .assertSamples(10, 100, 1000);

        new IterationsProgressionChecker()
                .setSamples(2)
                .assertSamples(5, 50, 500, 5000);

        new IterationsProgressionChecker()
                .setSamples(1)
                .assertSamples(1, 10, 100, 1000);

    }

    private static class AssertCounter implements PerformanceConsumer {
        private static final long serialVersionUID = 1L;

        private long[] iterations;
        private int maxSamples;
        private int samples;
        private int counter;

        public AssertCounter setIterations(long... iterations) {
            this.iterations = iterations;
            return this;
        }

        public AssertCounter setSamples(final int maxSamples) {
            this.maxSamples = maxSamples;
            return this;
        }

        @Override
        public void consume(final String message,
                final LoopPerformances loopPerformances) {
            assertEquals(iterations[counter],
                    loopPerformances.getIterations());
            samples++;
            if (samples == maxSamples) {
                counter++;
                samples = 0;
            }
        }

    }

    private static class BaseMagnitudeProgressionChecker {
        private int baseTimes;
        private int magnitude;
        private int samples;

        public BaseMagnitudeProgressionChecker setBaseTimes(int baseTimes) {
            this.baseTimes = baseTimes;
            return this;
        }

        public BaseMagnitudeProgressionChecker setMagnitude(int magnitude) {
            this.magnitude = magnitude;
            return this;
        }

        public BaseMagnitudeProgressionChecker setSamples(int samples) {
            this.samples = samples;
            return this;
        }

        private void assertSamples(final long... iterations) {
            PerformanceTimerBuilder.createSingleThread()

            .addTest("counter", new Runnable() {

                @Override
                public void run() {
                }
            })

            .addPerformanceConsumer(new AssertCounter()
                        .setIterations(iterations)
                        .setSamples(samples))

            .instrumentedBy(new ProgressionPerformanceInstrumenter.Builder())
                    .setBaseAndMagnitude(baseTimes, magnitude)
                    .setSamplePerIterations(samples)
                    .build()
                    .executeSequence();
        }
    }

    private static class IterationsProgressionChecker {
        private int samples;

        public IterationsProgressionChecker setSamples(int samples) {
            this.samples = samples;
            return this;
        }

        private void assertSamples(final long... iterations) {
            PerformanceTimerBuilder.createSingleThread()

            .addTest("counter", new Runnable() {

                @Override
                public void run() {
                }
            })

            .addPerformanceConsumer(new AssertCounter()
                        .setIterations(iterations)
                        .setSamples(samples))

            .instrumentedBy(new ProgressionPerformanceInstrumenter.Builder())
                    .setIterationProgression(iterations)
                    .setSamplePerIterations(samples)
                    .build()
                    .executeSequence();
        }
    }

}
