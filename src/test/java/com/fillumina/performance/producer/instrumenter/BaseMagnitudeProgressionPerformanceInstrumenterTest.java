package com.fillumina.performance.producer.instrumenter;

import com.fillumina.performance.PerformanceTimerBuilder;
import org.junit.Test;

/**
 *
 * @author fra
 */
public class BaseMagnitudeProgressionPerformanceInstrumenterTest {

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
            final AssertIterationsPerformanceConsumer assertIterations =
                    new AssertIterationsPerformanceConsumer()
                        .setIterations(iterations)
                        .setSamplesPerIteration(samples);

            PerformanceTimerBuilder.createSingleThread()

            .addTest("counter", new Runnable() {

                @Override
                public void run() {
                }
            })

            .addPerformanceConsumer(assertIterations)

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder())
                    .setBaseAndMagnitude(baseTimes, magnitude)
                    .setSamplePerIterations(samples)
                    .build()
                    .executeSequence();

            assertIterations.assertIterationsNumber(iterations.length);
        }
    }
}
