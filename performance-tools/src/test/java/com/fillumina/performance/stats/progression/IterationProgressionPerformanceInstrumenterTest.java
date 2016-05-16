package com.fillumina.performance.stats.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
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

        private void assertSamples(final int... iterations) {
            final AssertIterationsPerformanceConsumer assertIterations =
                    new AssertIterationsPerformanceConsumer()
                        .setIterations(iterations)
                        .setSamplesPerIteration(samples);

            PerformanceTimerFactory.createSingleThreaded()

            .instrumentedBy(ProgressionPerformanceInstrumenter.builder()
                    .setIterationProgression(iterations)
                    .setSamplesPerStep(samples)
                    .setEliminateOutliers(false)
                    .setAddBaselineTest(false)
                    .setTimeout(30, TimeUnit.DAYS) // to allow debugging
                    .build())

            .addTest("counter", new AbstractTestable() {

                @Override
                public Object test() {
                    return null;
                }
            })

            .addPerformanceConsumer(assertIterations)

            .execute();

            assertIterations.assertIterationsNumber(iterations.length);
        }
    }

}
