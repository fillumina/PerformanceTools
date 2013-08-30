package com.fillumina.performance.producer.progression;

import com.fillumina.performance.PerformanceTimerBuilder;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationProgressionPerformanceInstrumenterTest {

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

    private static class IterationsProgressionChecker {
        private int samples;

        public IterationsProgressionChecker setSamples(int samples) {
            this.samples = samples;
            return this;
        }

        private void assertSamples(final long... iterations) {
            final AssertIterationsPerformanceConsumer assertIterations =
                    new AssertIterationsPerformanceConsumer()
                        .setIterations(iterations)
                        .setSamplesPerIteration(samples);

            PerformanceTimerBuilder.createSingleThreaded()

            .addTest("counter", new Runnable() {

                @Override
                public void run() {
                }
            })

            .addPerformanceConsumer(assertIterations)

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(iterations)
                    .setSamplesPerMagnitude(samples)
                    .build())
                .execute();

            assertIterations.assertIterationsNumber(iterations.length);
        }
    }

}
